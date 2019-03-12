package com.gl.softphone;

/**
 * Created by vinton on 2016/12/08,0008.
 * Used for Video Call auto adapter resolution, bitrate and frame rate
 */

public class VideoPresetAdapter {
	/* low performance CPU preset, CPU core num <= 2 */
    public VideoPresetCpu low;
    /* medium performance CPU preset, CPU core 2 < num <= 4 */
    public VideoPresetCpu medium;
    /* high performance CPU preset, CPU core num > 4 */
    public VideoPresetCpu high;
    
    public class VideoPresetCpu {
        public int complexity_w240;
        public int complexity_w360;
        public int complexity_w480;
        public int complexity_w720;

        public int bitrate_w240;
        public int bitrate_w360;
        public int bitrate_w480;
        public int bitrate_w720;

        public int framerate_w240;
        public int framerate_w360;
        public int framerate_w480;
        public int framerate_w720;

        public VideoPresetCpu() {
            complexity_w240 = complexity_w360 = complexity_w480 = complexity_w720 = 0;
            bitrate_w240 = bitrate_w360 = bitrate_w480 = bitrate_w720 = 0;
            framerate_w240 = framerate_w360 = framerate_w480 = framerate_w720 = 0;
        }
    }
    
    public VideoPresetAdapter() {
		low = new VideoPresetCpu();
		medium = new VideoPresetCpu();
		high = new VideoPresetCpu();
	}
}
