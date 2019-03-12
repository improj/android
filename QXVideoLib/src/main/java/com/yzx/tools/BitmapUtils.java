package com.yzx.tools;  

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory.Options;
import android.util.Log;

public class BitmapUtils {
	private static final String TAG = BitmapUtils.class.getName();
	
	/**
	 * @Description 锟斤拷锟斤拷锟斤拷压锟斤拷 ,锟侥硷拷
	 * @param pathName
	 * @param reqWidth
	 * @param reqHeight
	 * @return	压锟斤拷锟斤拷锟絙itmap
	 * @date 2017-2-16 锟斤拷锟斤拷8:40:57 
	 * @author xhb  
	 * @return Bitmap    锟斤拷锟斤拷锟斤拷锟斤拷
	 */
	public static Bitmap decodeSampledBitmapForFile(String pathName, int reqWidth, int reqHeight) {
		Options options = new Options();
		options.inJustDecodeBounds = true; // 只锟斤拷锟竭ｏ拷锟斤拷锟斤拷锟斤拷锟斤拷
		BitmapFactory.decodeFile(pathName, options);
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(pathName, options);
	}
	
	/**
	 * @Description 锟斤拷锟斤拷锟斤拷压锟斤拷 ,byte锟斤拷锟斤拷
	 * @param data
	 * @param reqWidth
	 * @param reqHeight
	 * @return	压锟斤拷锟斤拷锟絙itmap
	 * @date 2017-2-16 锟斤拷锟斤拷8:40:57 
	 * @author xhb  
	 * @return Bitmap    锟斤拷锟斤拷锟斤拷锟斤拷
	 */
	public static Bitmap decodeSampledBitmapForByteArray(byte[] data, int reqWidth, int reqHeight) {
		Options options = new Options();
		options.inJustDecodeBounds = true; // 只锟斤拷锟竭ｏ拷锟斤拷锟斤拷锟斤拷锟斤拷
		BitmapFactory.decodeByteArray(data, 0, data.length, options);
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeByteArray(data, 0, data.length, options);
	}

	/**
	 * @Description 锟斤拷锟斤拷锟斤拷锟斤拷锟?
	 * @param options
	 * @param reqWidth
	 * @param reqHeight
	 * @return	TODO(锟斤拷锟斤拷锟斤拷锟斤拷)	
	 * @date 2017-2-17 锟斤拷锟斤拷11:13:07 
	 * @author xhb  
	 * @return int    锟斤拷锟斤拷锟斤拷锟斤拷
	 */
	public static int calculateInSampleSize(Options options,
			int reqWidth, int reqHeight) {
		final int height = options.outHeight;
		final int width = options.outWidth;
		Log.d(TAG, "height:" + height); // 2560
		Log.d(TAG, "width:" + width); // 1920
		int inSampleSize = 1;
		if (height > reqHeight || width > reqWidth) {
			final int halfHeight = height / 2; 
			final int halfWidth = width / 2;
			while((halfHeight / inSampleSize) >= reqHeight 
					|| (halfWidth / inSampleSize) >= reqWidth) {
				inSampleSize *=2;
			}
//			int widthScale = Math.round((float) width / (float) reqWidth);  
//            int heightScale = Math.round((float) height / (float) reqHeight);  
//            //为锟剿憋拷证图片锟斤拷锟斤拷锟脚憋拷锟轿ｏ拷锟斤拷锟斤拷取锟斤拷弑锟斤拷锟斤拷锟叫★拷锟斤拷歉锟? 
//            inSampleSize = widthScale < heightScale ? widthScale : heightScale;  
		}
		Log.d(TAG, "inSampleSize:" + inSampleSize);
		return inSampleSize;
	}

	public static byte[] Bitmap2Byte(Bitmap bitmap) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(CompressFormat.JPEG, 100, baos);
		return baos.toByteArray();
	}

	/**
	 * @Description 图片锟斤拷转
	 * @param bm 锟斤拷要锟斤拷转锟斤拷图片
	 * @param orientationDegree 锟斤拷转锟角讹拷
	 * @return	锟斤拷转锟斤拷锟酵计?
	 * @date 2017-2-17 锟斤拷锟斤拷11:15:26 
	 * @author xhb  
	 * @return Bitmap    锟斤拷锟斤拷锟斤拷锟斤拷
	 */
	public static Bitmap adjustPhotoRotation(Bitmap bm, final int orientationDegree) {
		Matrix m = new Matrix();
		m.setRotate(orientationDegree, (float) bm.getWidth() / 2,
				(float) bm.getHeight() / 2);
		try {
			Bitmap bm1 = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),
					bm.getHeight(), m, true);
			return bm1;
		} catch (OutOfMemoryError ex) {
			ex.printStackTrace();
		}
		return null;
	}
	
	// 锟斤拷锟斤拷锟斤拷片
	public static boolean savePic(byte[] data, File savefile) {
		Log.d(TAG, "compress:" + data.length);
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(savefile);
			fos.write(data);
			fos.flush();
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return false;
	}
}
  
