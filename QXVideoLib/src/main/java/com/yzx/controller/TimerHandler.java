    
package com.yzx.controller;  

import java.util.Timer;
import java.util.TimerTask;

import com.gl.softphone.UGoAPIParam;
import com.gl.softphone.UGoManager;
import com.yzx.api.UCSCall;
import com.yzx.listenerInterface.VoipListenerManager;
import com.yzx.preference.UserData;
import com.yzx.tools.CallLogTools;
import com.yzxtcp.tools.CustomLog;

/**
 * @Title TimerHandler   
 * @Description  定时器处理类，创建和销毁
 * @Company yunzhixun  
 * @author xhb
 * @date 2016-9-22 下午6:05:59
 */
public class TimerHandler {
	private static TimerHandler timerHandler;
	private boolean isAudioRecording = false;//语音通话是否正在录音
//	private String callDelayId;     //延迟通话的通话ID
	private int calllogCycle = CallLogTools.CALLLOG_CYCLE_DEFAULT; //呼叫日志采样周期
	private int calllogCount = 0; //呼叫质量日志计数
	private String calllogLastMessage = null; //呼叫质量日志最后一条信息
	
	private Timer audioRecordimer;//录音及时计时器，最长录制3分钟
	private Timer answerTimer;		//通话计时器
	private Timer callDelayTimer;    //断线后通话延长计时器
	private Timer callLogSampleTimer;	//呼叫日志采样定时器
	private Timer callVideoPreviewTimer;	// 上传视频预览图片定时器，默认是3秒
	
	
	private TimerHandler(){}
	
	public static TimerHandler getInstance() {
		if(timerHandler == null) {
			synchronized (TimerHandler.class) {
				if(timerHandler == null) {
					timerHandler = new TimerHandler();
				}
			}
		}
		return timerHandler;
	}
	
	
	public int getCalllogCycle() {
		return calllogCycle;
	}

	public void setCalllogCycle(int calllogCycle) {
		this.calllogCycle = calllogCycle;
	}

	public int getCalllogCount() {
		return calllogCount;
	}

	public void setCalllogCount(int calllogCount) {
		this.calllogCount = calllogCount;
	}

	public void setCalllogLastMessage(String calllogLastMessage) {
		this.calllogLastMessage = calllogLastMessage;
	}

	public boolean getAudioRecording() {
		return isAudioRecording;
	}

	public void setAudioRecording(boolean isAudioRecording) {
		this.isAudioRecording = isAudioRecording;
	}

	/**
	 * @Description 启动音频录音定时器，最多录制3分钟
	 * @date 2016年1月22日 下午2:18:19 
	 * @author zhj  
	 * @return void    返回类型
	 */
	public void startAudioRecordimer() {
	    stopAudioRecordimer();
	    if (audioRecordimer == null) {
	        audioRecordimer = new Timer();
	    }
	    audioRecordimer.schedule(new TimerTask() {
            @Override
            public void run() {
                CustomLog.v("录音满3分钟 ... ");
                UCSCall.StopRecord();
                isAudioRecording = false;
            }
        }, 60000 * 3);
	}
	
	public void stopAudioRecordimer() {
	    if (audioRecordimer != null) {
	        audioRecordimer.cancel();
	        audioRecordimer = null;
	    }
	}
	
	/**
	 * 30秒定时器/如果接听了就上报通话质量
	 * 
	 * @author: xiaozhenhua
	 * @data:2015-7-8 下午5:08:11
	 */
	public void startAnswerTimer(){
		stopAnswerTimer();
		stopCallDelayTimer();
		if(answerTimer == null){
			answerTimer = new Timer();
		}
		answerTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				CustomLog.v("TIME TASK ANSWER ... ");
				UserData.saveAnswer(VoipCore.getContext(), true);
			}
		}, 30000);
	}
	
	public void stopAnswerTimer(){
		if (answerTimer != null){
			answerTimer.cancel();
			answerTimer=null;
			CustomLog.v("STOP TIME TASK ANSWER ... ");
		}
	}
	
	/**
	 * @Description 启动通话延长计时器（断线情况下）
	 * @date 2016年1月12日 下午3:55:46 
	 * @author zhj  
	 * @return void    返回类型
	 */
	public void startCallDelayTimer(){
	    stopCallDelayTimer();
        if(callDelayTimer == null){
            callDelayTimer = new Timer();
        }
        CustomLog.v("启动通话延长计时器（断线情况下）");
        //断线10秒钟后断开电话
        callDelayTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                String callID = UCSCall.getCurrentCallId();
                if (callID != null && callID.length() > 0 /*&& callDelayId != null && callDelayId.length() > 0 && callID.equals(callDelayId)*/)
                {
//                    hangUp(callDelayId);
                	UGoManager.getInstance().pub_UGoHangup(UGoAPIParam.eUGo_Reason_HungupMyself);
                    CustomLog.v("通话延长计时器超时,挂断电话");
                }
            }
        }, 10*1000);
    }
    
    public void stopCallDelayTimer(){
        if (callDelayTimer != null){
            callDelayTimer.cancel();
            callDelayTimer=null;
//            callDelayId = null;
            CustomLog.v("STOP TIME DELAY TASK ... ");
        }
    }
    
	/**
	 * @Description 启动呼叫日志采样计时器
	 * @return void    返回类型 
	 * @date 2016年8月16日 下午12:07:17 
	 * @author zhj
	 */
    public void startCallLogSampleTimer() {
    	calllogCycle = CallLogTools.getCallLogCycle();
    	CustomLog.v("startCallLogSampleTimer calllogCycle: " + calllogCycle);
    	stopCallLogSampleTimer();
    	if (callLogSampleTimer == null) {
    		callLogSampleTimer = new Timer();
    	}
    	calllogLastMessage = null; 
    	callLogSampleTimer.schedule(new TimerTask() {

			@Override
			public void run() {
				if (calllogCount < CallLogTools.QUALITY_MAX_ITEMS) {
					CallLogTools.saveCallQuality(calllogLastMessage);
					calllogLastMessage = null; 
					calllogCount++;
				}
			}
    		
    	}, calllogCycle * 1000, calllogCycle * 1000);
	}

    /**
     * @Description 停止呼叫日志采样计时器
     * @return void    返回类型 
     * @date 2016年8月16日 下午12:08:19 
     * @author zhj
     */
	public void stopCallLogSampleTimer() {
		if (callLogSampleTimer != null) {
			callLogSampleTimer.cancel();
			callLogSampleTimer = null;
			CustomLog.v("stopCallLogSampleTimer ... ");
		}
	}
	
	/**
	 * @Description 开启上传视频预览图片定时器，如果到规定时间还没有上传成功，直接拨打电话 
	 * @param time	延迟时间
	 * @param callType	呼叫类型
	 * @param calledNumber	被叫号码
	 * @param userData		
	 * @date 2017-2-20 下午12:09:02 
	 * @author xhb  
	 * @return void    返回类型
	 */
	public void startVideoPreviewTimer(final int time) {
		stopVideoPreviewTimer();
		if(callVideoPreviewTimer == null) {
			callVideoPreviewTimer = new Timer();
		}
		CustomLog.v("startVideoPreviewTimer ... ");
		// 启动上传视频预览图片定时器，如果3秒后还没有上传成功就直接拨打电话。
		callVideoPreviewTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				CustomLog.v(time + "秒后还没有上传成功，直接拨打电话。。。");
				stopVideoPreviewTimer(); // 用完定时器后，销毁这个定时器，定时器就是一个线程，减少系统开销
				if(VoipListenerManager.getInstance().getPreviewImgUrlListener() != null) {
					VoipListenerManager.getInstance().getPreviewImgUrlListener().callback();
					VoipListenerManager.getInstance().setPreviewImgUrlListener(null); // 设置为空
				}
			}
		}, time);
	}
	
	/**
	 * @Description 停止上传视频预览图片定时器
	 * @date 2017-2-20 下午12:10:05 
	 * @author xhb  
	 * @return void    返回类型
	 */
	public void stopVideoPreviewTimer() {
		if(callVideoPreviewTimer != null) {
			callVideoPreviewTimer.cancel();
			callVideoPreviewTimer = null;
			CustomLog.v("stopVideoPreviewTimer ... ");
		}
	}
}
  
