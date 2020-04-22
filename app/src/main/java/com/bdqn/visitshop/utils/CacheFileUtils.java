package com.bdqn.visitshop.utils;
import android.os.Environment;
import android.text.TextUtils;

import java.io.File;

/**
 * 缓存文件工具类 在本地创建照片文件，获取照片绝对路径
 */
public class CacheFileUtils {
	private static final String KEY_SDCARD = "/sdcard";
	private static String name;

	public  static String getUpLoadPhotoPath() {
		StringBuffer fileSB = new StringBuffer();
		name = System.currentTimeMillis()+"";
		String key = String.format("%s.jpg", name);
		fileSB.append(getUpLoadPhotosRootPath()).append(File.separator).append(key);
		return fileSB.toString();
	}

	public static String getUpLoadPhotosPath() {
		StringBuffer fileSB = new StringBuffer();
		name = System.currentTimeMillis()+"";
		String key = String.format("%s%s.jpg", "china_bdqn", name);/*china_bdqn name.jpg 照片名称*/
		fileSB.append(getUpLoadPhotosRootPath()).append(File.separator).append(key);
		return fileSB.toString();/*返回绝对路径*/
	}
	public static String getUpLoadPhotosRootPath() {
		String sdcardPath = getSDPath();
		StringBuffer fileSB = new StringBuffer();
		fileSB.append(sdcardPath).append(File.separator).append("wfsf");
		fileSB.append(File.separator).append("uplaod_photo");
		String rootPath = fileSB.toString();
		File destDir = new File(rootPath);
		if (!destDir.exists() || destDir.getAbsoluteFile() == null) {
			destDir.mkdirs();/*手机下生成文件夹*/
		}
		return rootPath;
	}
	/**
	 * 获取sdk路径
	 */
	public static String getSDPath() {
		File sdDir = null;
		if (isSDCardAvaiable()) {
			sdDir = Environment.getExternalStorageDirectory();
		} else {
			sdDir = Environment.getRootDirectory();
		}
		if (sdDir != null && !TextUtils.isEmpty(sdDir.getPath())) {
			return sdDir.getPath();
		} else {
			return KEY_SDCARD;
		}
	}
	/**
	 * 判断sdk是否存在
	 */
	public static boolean isSDCardAvaiable() {
		try {
			return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) || !Environment.isExternalStorageRemovable();
		} catch (Exception e) {
		}
		return false;
	}
}
