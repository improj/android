package com.yzx.listenerInterface;

import com.yzx.api.UCSCameraType;
import com.yzxtcp.data.UcsReason;

/**
 * 锟界话状态锟斤拷锟斤拷锟斤拷
 * 
 * @author xiaozhenhua
 * 
 */
public interface CallStateListener {

	/**
	 * VOIP锟斤拷锟斤拷状态
	 * 
	 * @param reason
	 *            :锟轿匡拷VOIP锟斤拷锟斤拷状态锟斤拷
	 * @author: xiaozhenhua
	 * @data:2014-4-21 锟斤拷锟斤拷12:50:37
	 */
	public void onDialFailed(String callId, UcsReason reason);

	/**
	 * 锟斤拷锟斤拷锟斤拷锟斤拷
	 * 
	 * @param phone
	 *            :锟斤拷锟斤拷牡缁帮拷藕锟斤拷锟斤拷锟斤拷cliend锟剿伙拷(voipaccount)
	 * @author: xiaozhenhua
	 * @data:2014-4-21 锟斤拷锟斤拷12:51:16
	 */
	public void onIncomingCall(String callId, String callType,
							   String callerNumber, String nickName, String userdata);

	/**
	 * 锟揭讹拷锟铰硷拷锟截碉拷
	 * 
	 * @param reason
	 * @author: xiaozhenhua
	 * @data:2014-4-21 锟斤拷锟斤拷2:36:03
	 */
	public void onHangUp(String callId, UcsReason reason);

	/**
	 * 锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷
	 * 
	 * @param callId
	 * @author: xiaozhenhua
	 * @data:2014-4-29 锟斤拷锟斤拷2:30:08
	 */
	public void onAlerting(String callId);

	/**
	 * 锟斤拷锟斤拷锟铰硷拷锟截碉拷
	 * 
	 * @author: xiaozhenhua
	 * @data:2014-4-21 锟斤拷锟斤拷2:34:49
	 */
	public void onAnswer(String callId);

	/**
	 * 锟斤拷锟斤拷状态锟截碉拷
	 * 
	 */
	public void onNetWorkState(int reason, String message);

	/**
	 * DTMF锟截碉拷
	 * 
	 * @param dtmf
	 * @author: xiaozhenhua
	 * @data:2015-3-16 锟斤拷锟斤拷11:33:44
	 */
	public void onDTMF(int dtmfCode);

	/**
	 * 锟斤拷频锟界话锟斤拷图锟截碉拷
	 * 
	 * @param videoCapFilePath
	 * @author: xiaozhenhua
	 * @data:2015-5-20 锟斤拷锟斤拷4:11:12
	 */
	public void onCameraCapture(String videoCapFilePath);

	/**
	 * 锟斤拷通锟截碉拷锟铰硷拷
	 * 
	 * @param reason
	 * @author: xiaozhenhua
	 * @data:2014-4-21 锟斤拷锟斤拷2:47:28
	 */
	public void singlePass(int reason);
	
	/**
	 * 锟皆讹拷锟斤拷频模式锟截碉拷
	 * @param cameraType 锟皆凤拷锟斤拷频模式
	 * @author xhb
	 * @data 2015-11-2锟斤拷锟斤拷3:19
	 */
	public void onRemoteCameraMode(UCSCameraType cameraType);
	
	/**
	 * 
	 * @Description    媒锟斤拷锟斤拷锟斤拷锟杰回碉拷  
	 * @param inMsg    锟斤拷锟斤拷锟皆硷拷锟斤拷莅锟?
	 * @param outMsg   锟斤拷锟杰猴拷锟斤拷锟斤拷锟斤拷
	 * @param inLen    锟斤拷锟斤拷锟斤拷某锟斤拷锟?
	 * @param outLen   锟斤拷锟斤拷锟斤拷某锟斤拷锟?
	 * @date 2016锟斤拷2锟斤拷29锟斤拷 锟斤拷锟斤拷2:52:18 
	 * @author zhj
	 * @return void    锟斤拷锟斤拷锟斤拷锟斤拷
	 */
	public void onEncryptStream(byte[] inMsg, byte[] outMsg, int inLen, int[] outLen);
	

	/**
	 * 
	 * @Description    媒锟斤拷锟斤拷锟斤拷锟杰回碉拷  
	 * @param inMsg    锟斤拷锟斤拷锟皆硷拷锟斤拷莅锟?
     * @param outMsg   锟斤拷锟杰猴拷锟斤拷锟斤拷锟斤拷
     * @param inLen    锟斤拷锟斤拷锟斤拷某锟斤拷锟?
     * @param outLen   锟斤拷锟斤拷锟斤拷某锟斤拷锟?  
	 * @date 2016锟斤拷2锟斤拷29锟斤拷 锟斤拷锟斤拷2:52:24 
	 * @author zhj
	 * @return void    锟斤拷锟斤拷锟斤拷锟斤拷
	 */
	public void onDecryptStream(byte[] inMsg, byte[] outMsg, int inLen, int[] outLen);
	
	/**
	 * @Description 使锟斤拷指锟斤拷锟斤拷锟斤拷值锟斤拷锟解部锟斤拷频锟斤拷锟斤拷锟借备锟斤拷锟叫筹拷始锟斤拷(锟斤拷锟斤拷时锟截碉拷)
	 * @param sample_rate	锟斤拷锟斤拷锟斤拷
	 * @param bytes_per_sample	锟斤拷锟斤拷锟斤拷锟?每锟斤拷锟斤拷锟斤拷锟斤拷纸锟斤拷锟?
	 * @param num_of_channels	通锟斤拷锟斤拷
	 * @date 2016-3-30 锟斤拷锟斤拷4:08:36 
	 * @author xhb  
	 * @return void    锟斤拷锟斤拷锟斤拷锟斤拷
	 */
	public void initPlayout(int sample_rate, int bytes_per_sample, int num_of_channels);
	
	/**
	 * @Description 使锟斤拷指锟斤拷锟斤拷锟斤拷值锟斤拷锟解部锟斤拷频锟借备锟侥采硷拷锟斤拷锟叫筹拷始锟斤拷(锟斤拷通时锟截碉拷)
	 * @param sample_rate	锟斤拷锟斤拷锟斤拷
	 * @param bytes_per_sample	锟斤拷锟斤拷锟斤拷锟?每锟斤拷锟斤拷锟斤拷锟斤拷纸锟斤拷锟?
	 * @param num_of_channels	通锟斤拷锟斤拷
	 * @date 2016-3-30 锟斤拷锟斤拷4:10:04 
	 * @author xhb  
	 * @return void    锟斤拷锟斤拷锟斤拷锟斤拷
	 */
	public void initRecording(int sample_rate, int bytes_per_sample, int num_of_channels);
	
	/**
	 * @Description 锟斤拷锟斤拷锟斤拷锟街革拷锟斤拷锟斤拷莩锟斤拷鹊锟絇CM锟斤拷锟斤拷锟斤拷锟解部锟借备锟斤拷锟叫诧拷锟斤拷
	 * @param outData	锟斤拷锟斤拷锟斤拷锟解部锟斤拷频锟借备锟斤拷锟脚碉拷锟斤拷锟斤拷
	 * @param outSize	锟斤拷锟捷筹拷锟斤拷
	 * @return	0锟斤拷锟缴癸拷锟斤拷-1锟斤拷失锟斤拷	
	 * @date 2016-3-30 锟斤拷锟斤拷4:11:50 
	 * @author xhb  
	 * @return int    锟斤拷锟斤拷锟斤拷锟斤拷
	 */
	public int writePlayoutData(byte[] outData, int outSize);
	
	/**
	 * @Description 锟斤拷锟解部锟斤拷频锟借备锟斤拷取指锟斤拷锟斤拷锟捷筹拷锟饺的采硷拷PCM锟斤拷锟斤拷
	 * @param inData	锟斤拷锟斤拷锟轿拷锟斤拷獠匡拷锟狡碉拷璞革拷杉锟斤拷锟斤拷锟斤拷锟?
	 * @param inSize	锟斤拷锟捷筹拷锟斤拷
	 * @return	0锟斤拷锟缴癸拷锟斤拷-1锟斤拷失锟斤拷	
	 * @date 2016-3-30 锟斤拷锟斤拷4:15:23 
	 * @author xhb  
	 * @return int    锟斤拷锟斤拷锟斤拷锟斤拷
	 */
	public int readRecordingData(byte[] inData, int inSize);
	
	/**
	 * @Description 锟斤拷频预锟斤拷图片透锟斤拷锟截碉拷 
	 * @param callId 通锟斤拷锟斤拷callid
	 * @param bytes 预锟斤拷图片锟斤拷锟斤拷锟斤拷
	 * @param code	code锟斤拷锟藉：锟斤拷锟斤拷 0 锟斤拷url锟角凤拷 1 锟斤拷锟斤拷锟斤拷失锟斤拷 2 
	 * @date 2017-2-23 锟斤拷锟斤拷2:03:37 
	 * @author xhb  
	 * @return void    锟斤拷锟斤拷锟斤拷锟斤拷
	 */
	public void onTransPreviewImg(String callId, byte[] bytes, int code);

}
