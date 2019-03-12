/**
 * Copyright (c) 2017 The KQuck project authors. All Rights Reserved.
 */
package com.gl.softphone;

/**
 * @author vinton
 * @date 2016-12-15
 * @description Play audio file config
 */
public class AudioPlayConfig {
	// audio play mode, see "file play mode" in UGoAPIParam.java
	public int playMode;
	// audio file store format, use when playMode is kME_FILE_PATHNAME
	public int fileFormat;
    // play audio file to local or play as microphone
    public boolean playRemote;
    // need play looping
    public boolean loopEnabled;
    // audio file path, use when playMode is kME_FILE_PATHNAME
    String filePath;
    // size of audio data to play, use when playMode is kME_FILE_STREAM
    public int dataSize;
    // audio data to play, use when playMode is kME_FILE_STREAM
    public byte[] audioData;
    
	/**
	 * 
	 */
	public AudioPlayConfig() {
		// TODO Auto-generated constructor stub
	}

}
