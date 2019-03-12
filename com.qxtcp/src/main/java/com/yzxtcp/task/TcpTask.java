package com.yzxtcp.task;

import java.util.ArrayDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Process;

/**
 * TCP执行者
 * 
 * @author zhuqian
 */
public abstract class TcpTask<Result> {
	private static TCPHandler tcpHandler;

	private static final int MESSAGE_POST_RESULT = 1;

/*	// cpu核数
	private static final int CPU_COUNT = Runtime.getRuntime()
			.availableProcessors();
	// 核心线程数
	private static final int CORE_POOL_SIZE = CPU_COUNT + 1;
	// 最大线程数
	private static final int MAX_POOL_SIZE = CPU_COUNT * 2 + 1;
	// 保活一个线程
	private static final int KEEP_ALIVE = 1;
	// 默认的线程工厂
	private static final ThreadFactory DEFAULT_THREADFACTORY = new ThreadFactory() {
		private final AtomicInteger threadCount = new AtomicInteger(1);

		@Override
		public Thread newThread(Runnable r) {
			return new Thread(r, "TCP Thread # "
					+ threadCount.getAndIncrement());
		}
	};
	// 工作队列
	private static final BlockingQueue<Runnable> TCPQUEUE = new LinkedBlockingDeque<Runnable>();*/
	// 默认线程池
//	private static final Executor THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(
//			CORE_POOL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE, TimeUnit.SECONDS,
//			TCPQUEUE, DEFAULT_THREADFACTORY);

	private static final Executor THRAD_SINGLE_EXECUTOR = Executors.newSingleThreadExecutor();
	
	private static final SerialExecutor SERIAL_EXECUTOR = new SerialExecutor();

	// 默认的执行者
	private static final Executor DEFAULT_EXECUTOR = SERIAL_EXECUTOR;

	private final Callable<Result> tcpRunnable;
	private final FutureTask<Result> mFuture;

	private final AtomicBoolean mCanceled = new AtomicBoolean();
	private final AtomicBoolean mTaskInvoked = new AtomicBoolean();

	private static class SerialExecutor implements Executor {
		// 一个个执行 接口的大小可变数组的实现。数组双端队列没有容量限制，不是线程安全
		final ArrayDeque<Runnable> mTasks = new ArrayDeque<Runnable>();
		// 即将执行
		Runnable mAlive;
		
		@Override
		public void execute(final Runnable command) {
			mTasks.offer(new Runnable() {
				@Override
				public void run() {
					try {
						command.run();
					} finally {
						scheduleNext();
					}
				}
			});
			if (mAlive == null) {
				scheduleNext();
			}
		}

		private synchronized void scheduleNext() {
			if ((mAlive = mTasks.poll()) != null) {
				THRAD_SINGLE_EXECUTOR.execute(mAlive);
			}
		}
	}

	/**
	 * 执行
	 */
	public final void execute() {
		onPreExecute();
		DEFAULT_EXECUTOR.execute(mFuture);
	}
	
	private static TCPHandler getHandlerInstance(){
		synchronized (TcpTask.class) {
			if(tcpHandler == null){
				tcpHandler = new TCPHandler();
			}
			return tcpHandler;
		}
	}

	private static class TCPHandler extends Handler {
		public TCPHandler() {
			super(Looper.getMainLooper());
		}

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MESSAGE_POST_RESULT:
				// 执行结束
				TaskResult result = (TaskResult) (msg.obj);
				result.task.finish(result.data[0]);
				break;
			default:
				break;
			}
		}
	}

	public TcpTask() {
		tcpHandler = new TCPHandler();
		tcpRunnable = new Callable<Result>() {
			@Override
			public Result call() throws Exception {
				mTaskInvoked.set(true);
				Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
				return postResult(doInBackground());
			}
		};
		mFuture = new FutureTask<Result>(tcpRunnable) {
			@Override
			protected void done() {
				try {
					postResultIfNotInvoked(get());
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				} catch (CancellationException e) {
					postResultIfNotInvoked(null);
				}
			}
		};
	}

	/**
	 * 将结果发送到主线程
	 * 
	 * @param result
	 */
	private Result postResult(Result result) {
		getHandlerInstance().obtainMessage(MESSAGE_POST_RESULT, new TaskResult(this,result)).sendToTarget();
		return result;
	}

	/**
	 * 将结果发送到主线程
	 * 
	 * @param result
	 */
	private void postResultIfNotInvoked(Result result) {
		final boolean wasTaskInvoked = mTaskInvoked.get();
		if (!wasTaskInvoked) {
			postResult(result);
		}
	}

	/**
	 * 获取tcp结果
	 * 
	 * @return
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	public final Result get() throws InterruptedException,
			ExecutionException {
		return mFuture.get();
	}

	/**
	 * 终止工作
	 * 
	 * @param mayInterruptIfRunning
	 * @return
	 */
	public final boolean cancel(boolean mayInterruptIfRunning) {
		mCanceled.set(true);
		return mFuture.cancel(mayInterruptIfRunning);
	}

	/**
	 * 是否终止工作
	 * 
	 * @return
	 */
	public final boolean isCancel() {
		return mCanceled.get();
	}

	/**
	 * 后台执行
	 * 
	 * @return
	 */
	public abstract Result doInBackground();

	/**
	 * 执行完之后
	 * 
	 * @param result
	 */
	public abstract void onPostExecute(Result result);

	/**
	 * 执行前，主线程运行
	 */
	public abstract void onPreExecute();

	protected void onCancelled(Result result) {
	}

	public void finish(Result result) {
		if (isCancel()) {
			onCancelled(result);
		} else {
			onPostExecute(result);
		}
	}

	/**
	 * 
	 * @author zhuqian
	 */
	public static class TaskResult<Data> {
		public final TcpTask task; 
		public final Data[] data;
		public TaskResult(TcpTask task,Data... data){
			this.task = task;
			this.data = data;
		}
	}
}
