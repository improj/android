package com.gl.softphone;

public class VideoRecordConfig {
    /* video record store path, max length: 255 */
    String fileName;
    // video record direction, 0 record remote video, 1 record local video
    int iDirect;
    // video record file type, default 0, only support .avi now
    int fileType;
    // video record frame width
    int width;
    // video record frame height
    int height;
    // video record bitrate, in kbps
    int bitrate;
    // video record framerate
    int framerate;

    public VideoRecordConfig(String fileName, int iDirect, int fileType,
                             int width, int height, int bitrate, int framerate) {
        this.fileName = fileName;
        this.iDirect = iDirect;
        this.fileType = fileType;
        this.width = width;
        this.height = height;
        this.bitrate = bitrate;
        this.framerate = framerate;
    }

    public VideoRecordConfig(String fileName, int iDirect) {
        this.fileName = fileName;
        this.iDirect = iDirect;
        this.fileType = 0;
        this.width = 640;
        this.height = 480;
        this.bitrate = 200;
        this.framerate = 12;
    }
}
