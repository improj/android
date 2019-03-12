package com.yzxIM.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.MediaRecorder.OnErrorListener;
import android.media.MediaRecorder.OnInfoListener;

import com.yzxIM.listener.RecordListener;
import com.yzxtcp.tools.CustomLog;

/**
 * 录音工具类
 *
 */
public class RecordingTools {

	
	private static RecordingTools recordTools;
	
	public static RecordingTools getInstance(){
		if(recordTools == null){
			recordTools = new RecordingTools();
		}
		return recordTools;
	}
	
	private MediaRecorder mRecorder = null; // 录音
	private boolean isStop = false;
	private Object obj = new Object();
	private MediaPlayer mPlayer = null; // 播放
	private MediaPlayer mGetDurationPlay;//获取音频文件时长
	
	
	/**
	 * 获取录音文件的时长
	 * @param filePath
	 * @return
	 */
	public synchronized int getDuration(String filePath){
		int duration = 0;

		FileInputStream fis = null;
		try {
			fis =new FileInputStream(filePath);
			if (fis != null){
				if(mGetDurationPlay == null){
					mGetDurationPlay = new MediaPlayer();
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			clearPlay();
			return 100;
		}
		
		if (fis != null && mGetDurationPlay!=null){
			try {
				mGetDurationPlay.setDataSource(fis.getFD());
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
				try {
					if (fis != null){
						fis.close();
					}
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				clearPlay();
				return 0;
			} catch (IllegalStateException e) {
				e.printStackTrace();
				try {
					if (fis != null){
						fis.close();
					}
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				clearPlay();
				return 0;
			} catch (IOException e) {
				e.printStackTrace();
				try {
					if (fis != null){
						fis.close();
					}
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				clearPlay();
				return 100;
			}
		}
		
		try {
			if(null != mGetDurationPlay){
				mGetDurationPlay.prepare();
			}
		} catch (IllegalStateException e1) {
			e1.printStackTrace();
			try {
				if (fis != null){
					fis.close();
				}
			} catch (IOException e11) {
				e11.printStackTrace();
			}
			clearPlay();
		} catch (IOException e1) {
			try {
				if (fis != null){
					fis.close();
				}
			} catch (IOException e11) {
				e11.printStackTrace();
			}
			clearPlay();
			e1.printStackTrace();
		}
		
		if (mGetDurationPlay != null){
			duration = mGetDurationPlay.getDuration();
		}
		try {
			if (fis != null){
				fis.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		clearPlay();
		return duration;
	}

	public synchronized void clearPlay(){
		if (mGetDurationPlay != null) {
			mGetDurationPlay.reset();
			mGetDurationPlay.release();
			mGetDurationPlay = null;
		}
	}
	/**
	 * 开始录音
	 * @param toUid:接收者的UID
	 */
	public boolean startVoiceRecord(final String recordPath,final RecordListener recordListener){
		boolean isRecord = true;
		stopVoiceRecord();
		if(mRecorder == null){
			mRecorder = new MediaRecorder();
			mRecorder.setOnInfoListener(new OnInfoListener() {
				public void onInfo(MediaRecorder mr, int what, int extra) {
					CustomLog.v( "recorder Info: what=" + what + " extra=" + extra);
				}
			});
			mRecorder.setOnErrorListener(new OnErrorListener() {
				public void onError(MediaRecorder mr, int what, int extra) {
					CustomLog.e( "recorder onError: what=" + what + " extra=" + extra);
				}
			});
		}
		//final String recordPath = FileWRTools.createAudioFileName(toUid);
		
		try {
			mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			mRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
			mRecorder.setOutputFile(recordPath);
			mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
			mRecorder.prepare();
			mRecorder.start();
			isStop = true;
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						synchronized (obj) {
							obj.wait(60*1000+400);
						}
						if(isStop){
							CustomLog.v("自动停止录音  ... ");
							stopVoiceRecord();
						}
						int duration = 0;
						
						if(null!=recordPath){
							duration = getDuration(recordPath);
						}
						CustomLog.v("record voice duration:" + duration);
						if(duration/1000>0 && duration/1000<65){
							if((duration/1000)+(duration%1000>=500?1:0) > 60) {
								recordListener.onFinishedRecordingVoice(60);
							} else {
								recordListener.onFinishedRecordingVoice((duration/1000)+(duration%1000>=500?1:0));
							}
						}else if(duration==100){
							recordListener.onFinishedRecordingVoice(100);
						}else{
							recordListener.onFinishedRecordingVoice(0);
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}).start();
		} catch (IllegalStateException e) {
			isRecord = false;
			recordListener.onFinishedRecordingVoice(0);
			e.printStackTrace();
		} catch (IOException e) {
			isRecord = false;
			recordListener.onFinishedRecordingVoice(0);
			e.printStackTrace();
		}catch (Exception e){
			isRecord = false;
			recordListener.onFinishedRecordingVoice(0);
			e.printStackTrace();
		}
		return isRecord;
		//return recordPath;
	}
	
	/**
	 * 停卡录音
	 * 
	 */
	public synchronized void stopVoiceRecord(){
		if(mRecorder != null){
			synchronized (obj) {
				isStop = false;
				obj.notifyAll();
			}
			CustomLog.v("手动停止录音  ... ");
			try {
				mRecorder.stop();
				mRecorder.release();
			} catch (Exception e) {
				// TODO: handle exception
			}
			mRecorder = null;
		}
	}
	
	/**
	 * 当前是否正在播放
	 * @return
	 */
	public boolean isPlaying() {
		return (mPlayer != null) ? mPlayer.isPlaying() : false;
	}
	
	/**
	 * 播放录音
	 * @param filePath:需要播放的文件路径
	 * @param playerCompletionListener:播放完成的监听器(不需要回调时可以为空)
	 */
	public void startPlayerVoice(String filePath,final RecordListener recordListener){
		stopPlayerVoice();
		if(mPlayer == null){
			mPlayer = new MediaPlayer();
		}
		try {
			File file = new File(filePath);
			FileInputStream fis = new FileInputStream(file);
			mPlayer.setDataSource(fis.getFD());
			mPlayer.prepare();
			mPlayer.start();
			fis.close();
			mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
				@Override
				public void onCompletion(MediaPlayer mp) {
					if(recordListener != null){
						recordListener.onFinishedPlayingVoice();
					}
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
			CustomLog.v( "startPlay IOException : " + e.toString());
			if(recordListener != null){
				recordListener.onFinishedPlayingVoice();
			}
		} catch(Exception e){
			e.printStackTrace();
			CustomLog.v( "startPlay Exception : " + e.toString());
			if(recordListener != null){
				recordListener.onFinishedPlayingVoice();
			}
		}
	}
	/**
	 * 停止播放
	 * 
	 */
	public void stopPlayerVoice(){
		if(mPlayer != null){
			CustomLog.v( "stop play");
			mPlayer.stop();
			mPlayer.reset();
			mPlayer.release();
			mPlayer = null;
		}
	}
}
