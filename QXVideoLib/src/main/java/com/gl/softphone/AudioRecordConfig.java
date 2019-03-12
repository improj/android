/**
 * Copyright (c) 2017 The KQuck project authors. All Rights Reserved.
 */
package com.gl.softphone;
import com.gl.softphone.UGoAPIParam;

/**
 * @author vinton
 * @date 2016-12-15
 * @description Record audio config
 */
public class AudioRecordConfig {
	// file path for record data to write
	public String filePath;
	// record direction, see "record mode" in UGoAPIParam.java
	public int recordMode;
    // record file store format, see "file format" in UGoAPIParam.java
	public int fileFormat;
    
	/**
	 * 
	 */
	public AudioRecordConfig() {
		// TODO Auto-generated constructor stub
		recordMode = UGoAPIParam.kME_RECORD_MODE_ALL;
		fileFormat = UGoAPIParam.kME_FileFormatWavFile;
	}

}
