package com.yzx.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Pattern;

import com.yzx.preference.UserData;

public class CpuTools {

	/**
	 * 
	 * [获取cpu类型和架构]
	 * 
	 * @return 三个参数类型的数组，第一个参数标识是不是ARM架构，第二个参数标识是V6还是V7架构
	 */
	private static void initCpuArchitecture() {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("/proc/cpuinfo")));
			try {
				String nameProcessor = "Features";
				String line ="";
				while((line= br.readLine()) != null){
					if(line.indexOf(":") > 0){
						String[] pair = line.split(":");
						//CustomLog.v("CPU_PARME:"+line+"    CPU_KEY:"+pair[0]+"    CPU_VALUE:"+pair[1]);
						String key = pair[0].trim();
						String val = pair[1].trim();
						if (key.compareTo(nameProcessor) == 0) {
/*							if(val.indexOf("neon") > 0){
								UserData.saveCpuType(7);
							}else{
								UserData.saveCpuType(6);
							}*/
							/*String n = "";
							for (int i = val.indexOf("ARMv") + 4; i < val.length(); i++) {
								String temp = val.charAt(i) + "";
								if (temp.matches("\\d")) {
									n += temp;
									break;
								}
							}
							CustomLog.v(val.substring(0,val.lastIndexOf(" ")));*/
							
						}
					}
				}
			} finally {
				if(br != null) {
					br.close();
					br = null;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获得cpu频率
	 * @return
	 * @author: xiongjijian
	 * @data:2014-10-31 12:29:57
	 */
	public static int getCurCpuFreq() {
		int result = 0;
		String kCpuInfoCurFreqFilePath = "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq";
		FileReader fr = null;
		BufferedReader br = null;
		try {
			fr = new FileReader(kCpuInfoCurFreqFilePath);
			br = new BufferedReader(fr);
			String text = br.readLine();
			result = Integer.parseInt(text.trim());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(br != null) {
				try {
					br.close();
					br = null;
				} catch (IOException e) {
					e.printStackTrace();  
				}
			}
			if(fr != null) {
				try {
					fr.close();
					fr = null;
				} catch (IOException e) {
					e.printStackTrace();  
				}
			}
		}
		return result / 1000;
	}

	public static int getNumCores() {
		// Private Class to display only CPU devices in the directory listing
		class CpuFilter implements FileFilter {
			@Override
			public boolean accept(File pathname) {
				// Check if filename is "cpu", followed by a single digit number
				if (Pattern.matches("cpu[0-9]", pathname.getName())) {
					return true;
				}
				return false;
			}
		}

		try {
			// Get directory containing CPU info
			File dir = new File("/sys/devices/system/cpu/");
			// Filter to only list the devices we care about
			File[] files = dir.listFiles(new CpuFilter());
//			CustomLog.v("CPU Count: " + files.length);
			// Return the number of cores (virtual CPU devices)
			return files.length;
		} catch (Exception e) {
			// Print exception
//			CustomLog.v("CPU Count: Failed.");
			e.printStackTrace();
			// Default to return 1 core
			return 1;
		}
	}

}
