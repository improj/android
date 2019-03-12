package com.gl.softphone;

/**
 * @author：Rambo.Fu
 * @date： 2014-4-3
 * @description：all api params/macro/enum definitions
 */
public class UGoAPIParam {
    /**
     * *UGo module state****
     */
    public static final int eUGo_STATE_WAIT = 0;        //wait
    public static final int eUGo_STATE_INVITE = 1;        //invite
    public static final int eUGo_STATE_RINGING = 2;     //ringing
    public static final int eUGo_STATE_CONNECT = 3;        //connect
    public static final int eUGo_STATE_PUSHACTIVE = 4;    //ios push call active
    public static final int eUGo_STATE_SYSACTIVE = 5;    //system call active

    /**
     * *UGo event type******
     */
    public enum EventTypeEnum {
        eUGo_CALLDIALING_EV,        //call dialing event
        eUGo_CALLINCOMING_EV,        //call incoming event
        eUGo_CALLANSWER_EV,            //call answer event
        eUGo_CALLHUNGUP_EV,            //call hungup event
        eUGo_CALL_TRANS_STATE_EV,   // call internal trans state
        eUGo_NETWORK_EV,            //network state event
        eUGo_UPSINGLEPASS_EV,        //UP RTP single pass
        eUGo_DNSINGLEPASS_EV,        //DN RTP single pass
        eUGo_TCPTRANSPORT_EV,    //tcptransport event only use for test
        eUGo_CONFERENCE_EV,        //conference call event  add by wuzhaoyang20140605
        eUGo_GETDTMF_EV,            //get dtmf event
        eUGo_VIDEO_EV,
        eUGo_REMOTE_VIDEO_STATE_NOTIFY_EV
    }

    /**
     * *network state reason******
     */
    public static final int eUGo_NETWORK_NICE = 0;           // nice, very good
    public static final int eUGo_NETWORK_WELL = 1;           // well, good
    public static final int eUGo_NETWORK_GENERAL = 2;        // general
    public static final int eUGo_NETWORK_POOR = 3;           // poor
    public static final int eUGo_NETWORK_BAD = 4;             // bad

    /**
     * *the reason for singlepass event**
     */
    public static final int eUGo_NETWORK_ERROR = 0;                //network problem
    public static final int eUGo_AUDIO_DEVICE_INIT = 1;            //local device init failed
    public static final int eUGo_START_SEND = 2;                //start send failed
    public static final int eUGo_START_RECEIVE_FAIL = 3;        //start receive failed
    public static final int eUGo_SET_LOCAL_RECEIVER_FAIL = 4;    //receive failed


    /****UGo event reason: begin*****/
    /**
     * success reason 0*
     */
    public static final int eUGo_Reason_Success = 0;    //success

    /*public reason 1~29*/
    public static final int eUGo_Reason_NotAccept = 1;    //not accept
    public static final int eUGo_Reason_RtppTimeOut = 2;    //RTPP RTP timeout
    public static final int eUGo_Reason_NoBalance = 3;    //nobalance
    public static final int eUGo_Reason_UpdateMediaFail = 4;    //update media fail
    public static final int eUGo_Reason_Busy = 5;    //busy
    public static final int eUGo_Reason_Reject = 6;    //reject
    public static final int eUGo_Reason_NotFind = 7;    //not find
    public static final int eUGo_Reason_TooShort = 8;    //Forbidden(callee too short)
    public static final int eUGo_Reason_CalleeFrozen = 9;    //callee have been frozen
    public static final int eUGo_Reason_Freeze = 10;    //caller have been frozen
    public static final int eUGo_Reason_Expired = 11;    //caller expired
    public static final int eUGo_Reason_Cancel = 12;    //Terminater for Cancel
    public static final int eUGo_Reason_Forbidden = 13;    //Forbidden(caller binding number)
    public static final int eUGo_Reason_NoResponse = 14;    //no response timeout
    public static final int eUGo_Reason_NetworkDisable = 15;   //The network is not supported
    public static final int eUGo_Reason_UnReachable = 16;   //Signaling inaccessible(NACK)
    public static final int eUGo_Reason_UnableToPush = 17;   //ios unable to push
    public static final int eUGo_Reason_CallidNotExist = 18;   //callid is not exist
    public static final int eUGo_Reason_NoAnswer = 19;    //callee have no answer
    public static final int eUGo_Reason_ConnectFaild = 20;    //connect faild
    public static final int eUGo_Reason_BlackList = 23;     // blacklist

    /*client reason 30~49*/
    public static final int eUGo_Reason_HungupMyself = 30;   //call hungup by myself
    public static final int eUGo_Reason_HungupPeer = 31;   //call hungup by peer
    public static final int eUGo_Reason_HungupTCPDisconnected = 32;   //"Tcp event:Server connect failed!!!"
    public static final int eUGo_Reason_HungupRTPTimeout = 33;   //"medie engine: RTP time out!!!"
    public static final int eUGo_Reason_StartSendFailed = 34;   // start send failed

    /**
     * vps sever reason 50~79*
     */
    public static final int eUGo_Reason_ProxyAuth = 50;    //proxy auth
    public static final int eUGo_Reason_MsgHeadError = 51;    //message head error
    public static final int eUGo_Reason_MsgBodyError = 52;    //message body error
    public static final int eUGo_Reason_CallIDExist = 53;    //callid exist
    public static final int eUGo_Reason_MsgTimeOut = 54;    //message timeout
    public static final int eUGo_Reason_UserIdNotExist = 55;  // Called userid not exist
    public static final int eUGo_Reason_VpsGroupHunpup = 56; // Group Call Hangup By Vps


    /* conference reason  add by wuzhaoyang20140621 */
    public static final int eUGo_Reason_CONF_NO_EXIST = 60;  //find the conference fail
    public static final int eUGo_Reason_CONF_STATE_ERR = 61;  //conference state error
    public static final int eUGo_Reason_CONF_FULL = 62;  //conference full
    public static final int eUGo_Reason_CONF_CREATE_ERR = 63;  //create conference fail
    public static final int eUGo_Reason_CONF_CALL_FAILED = 64;  //call procedure fail
    public static final int eUGo_Reason_CONF_MEDIA_FAILED = 65;  //apply media resource fail
    public static final int eUGo_Reason_CONF_TER_UNSUPPORT = 66;  //the peer don't support
    public static final int eUGo_Reason_CONF_PARTICIPANT_OVER = 67; //too many participant
    public static final int eUGo_Reason_CONF_FINISHED = 68;  // conference finished

    /* conference reason  add by wuzhaoyang20140605 */
    public static final int eUGo_Reason_StateNotify = 70;   //conference state notify
    public static final int eUGo_Reason_ActiveModeConvert = 71;   //active change to conference mode
    public static final int eUGo_Reason_PassiveModeConvert = 72;   //passive change to conference mode

    /*temporary reason 80~98(notify:80~89,other:90~98)*/
    public static final int eUGo_Reason_NotifyPeerNotFind = 80;   //call notify peer uid not find
    public static final int eUGo_Reason_NotifyPeerOffLine = 81;   //call notify peer offline
    public static final int eUGo_Reason_NotifyPeerTimeout = 82;   //call notify free call timeout

    public static final int eUGo_Reason_Connecting = 97;    //connecting between send invite and don't receive response
    public static final int eUGo_Reason_Ringing = 98;   //Ringing response
    public static final int eUGo_Reason_UnkownError = 99;    //unkown error
    /****UGo event reason: end*****/

    /**
     * Video state *
     */
    /* idle */
    public static final int eUGo_Video_State_Idle = 0x00;
    /* start/stop camera capture */
    public static final int eUGo_Video_State_Camera_Capture = 0x01;
    /* start/stop local media send */
    public static final int eUGo_Video_State_Media_Send = 0x02;
    /* start/stop local media received */
    public static final int eUGo_Video_State_Media_Receive = 0x04;
    /* start/stop local video preview */
    public static final int eUGo_Video_State_Local_Preview = 0x08;
    /* start/stop remote video render */
    public static final int eUGo_Video_State_Remote_Render = 0x10;
    /* start/stop all */
    public static final int eUGo_Video_State_All = 0x1F;


    /**
     * * TCP transport state ***
     */
    public static final int eUGo_TCP_DISCONNECTED = 0;
    public static final int eUGo_TCP_CONNECTED = 1;
    public static final int eUGo_TCP_RECONNECTED = 2;

    /**
     * Network type
     */
    public static final int eUGo_Network_type_WIFI = 1;
    public static final int eUGo_Network_Type_2G = 2;
    public static final int eUGo_Network_type_3G = 4;
    public static final int eUGo_Network_type_4G = 8;

    /**
     * system call state*
     */
    public static final int eUGo_SYSCALL_IDLE = 0;
    public static final int eUGo_SYSCALL_ACTIVE = 1;

    /**
     * **debug level **
     */
    public static final int eME_TraceNone = 0x0000;  // no trace
    public static final int eME_TraceStateInfo = 0x0001;
    public static final int eME_TraceWarning = 0x0002;
    public static final int eME_TraceError = 0x0004;
    public static final int eME_TraceDebug = 0x0800;    // debug
    public static final int eME_TraceInfo = 0x1000;  // debug info
    public static final int eME_TraceReport = 0x4000;  //calling report
    public static final int eME_TraceAll = 0xffff;

    /**
     * * call mode **
     */
    public static final int eUGo_CM_DIRECT = 4;
    public static final int eUGo_CM_AUTO = 5;
    public static final int eUGo_CM_FREE = 6;

    /**
     * * file play mode **
     */
    public static final int kME_FILE_HANDLE = 0;
    public static final int kME_FILE_STREAM = 1;
    public static final int kME_FILE_PATHNAME = 2;

    /**
     * **file format **
     */
    public static final int kME_FileFormatWavFile = 1;
    public static final int kME_FileFormatCompressedFile = 2;
    public static final int kME_FileFormatAviFile = 3;
    public static final int kME_FileFormatPreencodedFile = 4;
    public static final int kME_FileFormatPcm16kHzFile = 7;
    public static final int kME_FileFormatPcm8kHzFile = 8;
    public static final int kME_FileFormatPcm32kHzFile = 9;

    /**
     * ** record mode ** 
     */
    /**
     * record all(double directions voice)
     **/
    public static final int kME_RECORD_MODE_ALL = 0;
    /**
     * only record local microphone voice
     **/
    public static final int kME_RECORD_MODE_MIC = 1;
    /**
     * only record remote voice
     **/
    public static final int kME_RECORD_MODE_SPEAKER = 2;

    /**
     * ** method id ***
     */
    public static final int UGO_CFG_PARAM_MODULE_ID = 0;        // Corresponding  UGo module parameter configuration structure
    public static final int UGO_CFG_TCP_MODULE_ID = 1;            // corresponding  UGo module TCP configuration structure
    public static final int UGO_CFG_ICE_MODULE_ID = 2;            // Corresponding UGo module ICE configuration structure
    public static final int UGO_RTPP_CFG_MODULE_ID = 3;            //Corresponding  UGo module RTPP configuration structure
    public static final int UGO_EMODEL_MODULE_ID = 4;            //corresponds EMODEL module configuration structure (reservation)


    public static final int ME_CTRL_CFG_MODULE_ID = 100;            //Engine control module configuration corresponding media structure
    public static final int ME_VQE_CFG_MODULE_ID = 101;            //module configuration structure corresponding vqe
    public static final int ME_RTP_CFG_MODULE_ID = 102;            // rtp module configuration corresponding structure
    public static final int ME_ENV_CFG_MODULE_ID = 103;            //corresponding environment variables module configuration structure
    public static final int ME_VIDEO_ENC_CFG_MODULE_ID = 104; /* Encoder parameters included */
    public static final int ME_VIDEO_DEC_CFG_MODULE_ID = 105; /* Decoder parameters included */
    public static final int ME_VIDEO_RENDER_CFG_MODULE_ID = 106; /* Render parameters included */
    public static final int ME_VIDEO_PROCES_CFG_MODULE_ID = 107;  /* Image process parameters included */
    public static final int ME_CODECS_CFG_MODULE_ID = 108;  // corresponding codec module configuration structure
    public static final int ME_VIDEO_EXTERN_CAPTURE_CFG_MODULE_ID = 109;  /* Image process parameters included */
    public static final int ME_VIDEO_PRESET_ADAPTER_CFG_MODULE_ID = 110;
    /* Get supported video codecs or Set video codecs priority list */
    public static final int ME_VIDEO_CODEC_CFG_MODULE_ID = 111;

    // Application screen orientation
    public static final int kME_DEVICE_ORIENTATION_PORTRAIT = 0;
    public static final int kME_DEVICE_ORIENTATION_LANDSCAPE = 1;
    public static final int kME_DEVICE_ORIENTATION_REVERSE_PORTRAIT = 2;
    public static final int kME_DEVICE_ORIENTATION_REVERSE_LANDSCAPE = 3;

    /**
     * Video Profile preset id
     */
    public enum MeVideoProfilePreset {
        /* Camera capture preset suitable for 320x240 video output */
        kME_VIE_PROFILE_PRESET_320x240,
        /* Camera capture preset suitable for 352x288 video output */
        kME_VIE_PROFILE_PRESET_352x288,
        /* Camera capture preset suitable for 640x480 video output */
        kME_VIE_PROFILE_PRESET_640x480,
        /* Camera capture preset suitable for 1280x720 video output */
        kME_VIE_PROFILE_PRESET_1280x720
    }

    /*define struction*/
    public MediaConfig stMediaCfg = null;
    public RtpConfig stRTPCfg = null;


    //modity by charlie  yuan 2014.04.30
    //public static RtppSrvConfig[] astRTPSrvCfg = null;
    //public RtppSrvConfig astRTPSrvCfg = null;

    public EnvConfig envConfig = null;
    public DecodeConfig decodeConfig = null;
    public UGoConfig stUGoCfg = null;
    //add by charlie yuan 2014.04.29
    public TcpConfig stTcpCfg = null;
    public IceConfig stIceCfg = null;
    //end add by charlie
    public VqeConfig stVQECfg = null;
    public VideoEncodeConfig videoEncodeConfig = null;
    public VideoDecodeConfig videoDecodeConfig = null;
    public VideoPresetAdapter videoPresetAdapter = null;

    public VideoRenderConfig videoRenderConfig = null;
    public VideoProcess stVideoProcessCfg = null;
    public VideoExternCapture stVideoExternCapture = null;
    public EmodelInfo stEmlInfoCfg = null;
    public EmodelValue stEmlValueCfg = null;

    public VideoCaptureConfig videoCaptureConfig = null;
    public LogTraceConfig logTraceConfig = null;
    public DialingConfig stDialingConfig = null;
    public AudioRecordConfig audioRecordConfig = null;
    public TcpRecvPara stTcpRecvPara = null;
    public AudioPlayConfig audioPlayConfig = null;
    public AudioInfo stAudioInfo = null;
    public VideoInfo stVideoInfo = null;
    public LogCfg logCfg = null;

    public ConferenceTestDialPara stConferenceTestDialPara = null;
    public NoramlCallTestDialPara stNmlCallTestDialPara = null;

    public static UGoAPIParam ugoApiParam;

    public static UGoAPIParam getInstance() {
        if (ugoApiParam == null) {
            ugoApiParam = new UGoAPIParam();
        }
        return ugoApiParam;
    }

    public static final int MAX_VIDEO_CODEC_LIST_NUM = 10;

    public UGoAPIParam() {
        stMediaCfg = new MediaConfig();
        stRTPCfg = new RtpConfig();
        //modity by charlie yuan 2014.04.30
        //astRTPSrvCfg = new RtppSrvConfig[2];

        //astRTPSrvCfg[0] = new RtppSrvConfig();
        //astRTPSrvCfg[1] = new RtppSrvConfig();
        //astRTPSrvCfg = new RtppSrvConfig();

        envConfig = new EnvConfig();
        decodeConfig = new DecodeConfig();
        stUGoCfg = new UGoConfig();
        stTcpCfg = new TcpConfig();
        stIceCfg = new IceConfig();
        //astRTPSrvCfg = new RtppSrvConfig();

        stVQECfg = new VqeConfig();
        videoEncodeConfig = new VideoEncodeConfig();
        videoPresetAdapter = new VideoPresetAdapter();

        videoRenderConfig = new VideoRenderConfig();
        videoDecodeConfig = new VideoDecodeConfig();
        stVideoProcessCfg = new VideoProcess();
        stVideoExternCapture = new VideoExternCapture();

        stEmlInfoCfg = new EmodelInfo();
        stEmlValueCfg = new EmodelValue();
        stEmlValueCfg.emodel_mos = new EmodelInfo();
        stEmlValueCfg.emodel_ie = new EmodelInfo();
        stEmlValueCfg.emodel_ppl = new EmodelInfo();
        stEmlValueCfg.emodel_burstr = new EmodelInfo();
        stEmlValueCfg.emodel_tr = new EmodelInfo();
        stEmlValueCfg.emodel_delay = new EmodelInfo();
        stEmlValueCfg.emodel_jb = new EmodelInfo();
        stEmlValueCfg.callInfo = new CallInfo();

        videoCaptureConfig = new VideoCaptureConfig();
        logTraceConfig = new LogTraceConfig();
        stDialingConfig = new DialingConfig();
        audioRecordConfig = new AudioRecordConfig();
        stTcpRecvPara = new TcpRecvPara();
        audioPlayConfig = new AudioPlayConfig();
        stAudioInfo = new AudioInfo();
        stVideoInfo = new VideoInfo();
        logCfg = new LogCfg();
        
        /* add by VintonLiu on 20141114 for conference auto test */
        stConferenceTestDialPara = new ConferenceTestDialPara();
        stNmlCallTestDialPara = new NoramlCallTestDialPara();

    }
}
