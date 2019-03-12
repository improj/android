package com.yzx.tools;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.gl.softphone.UGoAPIParam;
import com.gl.softphone.UGoManager;
import com.yzxtcp.tools.CustomLog;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.os.Build;
import android.os.Vibrator;

/**
 * 音频管理与播放提示音
 * @author xiaozhenhua
 *
 */
public class AudioManagerTools {

	private static AudioManagerTools audioPlayer ;
	public static AudioManagerTools getInstance(Context mContext){
		if(audioPlayer == null){
			audioPlayer = new AudioManagerTools(mContext);
		}
		return audioPlayer;
	}

	private Context mContext;
	private Vibrator mVibrator;
	private MediaPlayer mRingerPlayer;

	public AudioManagerTools(Context mC){
		mContext = mC;
		if(mContext != null){
			mVibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
		}
	}

	/**
	 * 播放来电音
	 * @param RingtoneManagerType
	 * @param vibrator
	 * @author: xiaozhenhua
	 * @data:2013-2-19 下午4:39:04
	 */
	public synchronized void startRinging(boolean isVibrator) {
		try {
			if(mContext != null && mVibrator != null){
				if (isVibrator) {
					long[] patern = { 0, 1000, 1000 };
					mVibrator.vibrate(patern, 1);
				}
				if (mRingerPlayer == null) {
					mRingerPlayer = new MediaPlayer();
				}
				mRingerPlayer.setDataSource(mContext, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE));
				mRingerPlayer.setAudioStreamType(AudioManager.STREAM_RING);
				mRingerPlayer.prepare();
				mRingerPlayer.setLooping(true);
				mRingerPlayer.start();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 关闭来电响铃
	 *
	 * @author: xiaozhenhua
	 * @data:2013-2-18 下午4:21:49
	 */
	public synchronized void stopRinging() {
		try {
			if (mRingerPlayer != null) {
				mRingerPlayer.stop();
				mRingerPlayer.release();
				mRingerPlayer = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (mVibrator!=null) {
			mVibrator.cancel();
		}
		if(android.os.Build.BRAND != null && (android.os.Build.BRAND.toString().contains("Xiaomi")
				|| android.os.Build.BRAND.toString().equals("Xiaomi"))){
			if(mContext != null){
				SystemMediaConfig.restoreMediaConfig(null,mContext);
			}
		}
	}
	/**
	 * 播放去电提示音
	 * @param mContext
	 * @author: xiaozhenhua
	 * @data:2013-2-18 下午4:56:28
	 */
	public void startCallRinging(final String fileName) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				if(mContext != null){
					byte[] dial = play_dialing_tone(fileName,mContext);
					if(dial != null){
						UGoAPIParam.getInstance().audioPlayConfig.audioData = dial;
						UGoAPIParam.getInstance().audioPlayConfig.fileFormat = UGoAPIParam.kME_FileFormatPcm16kHzFile;
						UGoAPIParam.getInstance().audioPlayConfig.playRemote = false;
						UGoAPIParam.getInstance().audioPlayConfig.loopEnabled = true;
						UGoAPIParam.getInstance().audioPlayConfig.playMode = 1;
						UGoAPIParam.getInstance().audioPlayConfig.dataSize = UGoAPIParam.getInstance().audioPlayConfig.audioData.length;
						int result = UGoManager.getInstance().pub_UGoPlayFile(UGoAPIParam.getInstance().audioPlayConfig);
						CustomLog.v("CURRENT_PLAYER:"+result);
					}else{
						CustomLog.v("CURRENT_FILE is null");
					}
				}else{
					CustomLog.v("CURRENT_PLAYER:ConnectionControllerService is null");
				}
			}
		}).start();
	}

	/**
	 * 停止去电提示音
	 *
	 * @author: xiaozhenhua
	 * @data:2013-2-18 下午4:56:12
	 */
	public void stopCallRinging() {
		CustomLog.v("CURRENT_STOP_PLAYER ... ");
		UGoManager.getInstance().pub_UGoStopFile();
	}
	
	/**
	 * @Description ²¥·Å×Ô¶¯ÓïÒô£¨Í¨»°½ÓÌýºó£¬²¥·Å¸ø¶Ô·½Ìý£©
	 * @param fileName ÓïÒôÎÄ¼þ£¬ÒÑ·ÅÔÚ¡°assets¡±ÖÐ
	 * @param bLoop ÊÇ·ñÑ­»·²¥·Å
	 * @return void    ·µ»ØÀàÐÍ 
	 * @date 2017Äê5ÔÂ9ÈÕ ÉÏÎç11:26:00 
	 * @author zhj
	 */
	public void startAutoVoice(final String fileName, final boolean bLoop) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				if(mContext != null){
					byte[] dial = play_dialing_tone(fileName,mContext);
					if(dial != null){
						UGoAPIParam.getInstance().audioPlayConfig.audioData = dial;
						UGoAPIParam.getInstance().audioPlayConfig.fileFormat = UGoAPIParam.kME_FileFormatPcm16kHzFile;
						UGoAPIParam.getInstance().audioPlayConfig.playRemote = true;
						UGoAPIParam.getInstance().audioPlayConfig.loopEnabled = bLoop;
						UGoAPIParam.getInstance().audioPlayConfig.playMode = 1;
						UGoAPIParam.getInstance().audioPlayConfig.dataSize = UGoAPIParam.getInstance().audioPlayConfig.audioData.length;
						int result = UGoManager.getInstance().pub_UGoPlayFile(UGoAPIParam.getInstance().audioPlayConfig);
						CustomLog.v("CURRENT_PLAYER:"+result);
					}else{
						CustomLog.v("CURRENT_FILE is null");
					}
				}else{
					CustomLog.v("CURRENT_PLAYER:ConnectionControllerService is null");
				}
			}
		}).start();
	}
	
	private OnAudioFocusChangeListener createOnAudioFocusChangeListener() {
		return new OnAudioFocusChangeListener() {
			@Override
			public void onAudioFocusChange(int focusChange) {
				if(AudioManager.AUDIOFOCUS_REQUEST_GRANTED == focusChange){
					CustomLog.v("音频资源审请成功");
				}else if(AudioManager.AUDIOFOCUS_REQUEST_FAILED == focusChange){
					CustomLog.v("音频资源审请失败");
				}else if( AudioManager.AUDIOFOCUS_GAIN == focusChange){
					CustomLog.v("获取音频资源");
				}else if( AudioManager. AUDIOFOCUS_LOSS == focusChange){
					CustomLog.v("失去音频资源");
				}
				CustomLog.v("ON_AUDIO_FOCUS_CHANGE:" + focusChange);
			}
		};
	}


	public static byte[] play_dialing_tone(String fileName,Context mContext) {
		//if (dialing_tone == null) {
		//dialing_tone = convertStream2byteArrry(fileName,mContext);
		//}
		//return dialing_tone;
		return convertStream2byteArrry(fileName,mContext);
	}

	/**
	 * 将音频文件转换成byteArray
	 * @param filepath
	 * @param mContext
	 * @return
	 * @author: xiaozhenhua
	 * @data:2014-7-23 下午12:37:50
	 */
	public static byte[] convertStream2byteArrry(String filepath, Context mContext) {
		InputStream inStream = null;
		//兼容studio开发环境  modifid by zhj 20151119
		//inStream = mContext.getResources().getAssets().open(filepath);
		inStream = mContext.getClass().getClassLoader().getResourceAsStream("assets/"+filepath);
		/*try {
			inStream = mContext.getResources().getAssets().open(filepath);
		} catch (IOException e) {
			e.printStackTrace();
		}*/

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int i = 0;
		byte[] mybyte = null;
		try {
			if(inStream != null){
				inStream.available();
				while ((i = inStream.read()) != -1) {
					baos.write(i);
				}
				inStream.close();
				mybyte = baos.toByteArray();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return mybyte;
	}

	/**
	 * 通话时用于设置免提或内放
	 * @param loudspeakerOn true:外放   false:内放
	 * @author: xiaozhenhua
	 * @data:2013-2-19 下午4:27:47
	 */
	public synchronized void setSpeakerPhoneOn(boolean loudspeakerOn) {
		UGoManager.getInstance().pub_UGoSetLoudSpeakerStatus(loudspeakerOn);
	}

	public boolean isSpeakerphoneOn(){
		return UGoManager.getInstance().pub_UGoGetLoudSpeakerStatus();
	}

	/**
	 * 判断品牌或机型是否需要设置AudioManager的Mode
	 * @return
	 * @author: xiaozhenhua
	 * @data:2013-4-10 上午11:56:11
	 */
	public boolean isMode(){
		String brandString = android.os.Build.BRAND;
		String modelString = "";
		if (Build.MODEL != null)
			modelString = Build.MODEL.replaceAll(" ", "");
		CustomLog.v("phone band ="+brandString);
		CustomLog.v("phone modelString = " + modelString);
		return brandString != null
				&&(brandString.equalsIgnoreCase("yusu")
				|| brandString.equalsIgnoreCase("yusuH701")
				|| brandString.equalsIgnoreCase("yusuA2")
				|| brandString.equalsIgnoreCase("qcom")
				|| brandString.equalsIgnoreCase("motoME525")
				|| (brandString.equalsIgnoreCase("Huawei")
				&& !modelString.equalsIgnoreCase("HUAWEIY220T")
				&& !modelString.equalsIgnoreCase("HUAWEIT8600")
				&& !modelString.equalsIgnoreCase("HUAWEIY310-T10")
				&& !modelString.equalsIgnoreCase("HUAWEIT8951"))
				|| brandString.equalsIgnoreCase("lge")
				|| brandString.equalsIgnoreCase("SEMC")
				|| (brandString.equalsIgnoreCase("ZTE")
				&& !modelString.equalsIgnoreCase("ZTEU880E")
				&& !modelString.equalsIgnoreCase("ZTEV985")
				&& !modelString.equalsIgnoreCase("ZTE-TU880")
				&& !modelString.equalsIgnoreCase("ZTE-TU960s")
				&& !modelString.equalsIgnoreCase("ZTEU793"))
				|| modelString.equalsIgnoreCase("LenovoS850e")
				|| modelString.equalsIgnoreCase("LenovoA60")
				|| modelString.equalsIgnoreCase("HTCA510e")
				|| (brandString.equalsIgnoreCase("Coolpad")
				&& modelString.equalsIgnoreCase("7260"))
				|| modelString.equalsIgnoreCase("Coolpad5890")
				|| brandString.equalsIgnoreCase("ChanghongV10")
				|| modelString.equalsIgnoreCase("MI2")
				|| modelString.equalsIgnoreCase("MI2S")
				|| modelString.equalsIgnoreCase("MT788")
				|| modelString.equalsIgnoreCase("MI-ONEPlus")
				|| modelString.equalsIgnoreCase("HUAWEIP6")
				|| modelString.equalsIgnoreCase("LenovoA780"));
	}
}
