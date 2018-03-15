package com.msl.util;

import java.io.File;
import java.io.UnsupportedEncodingException;

public class FileUtil {
	/**
	 * 获取Jar包运行的绝对路径
	 * @return jar包文件当前的绝对路径
	 */
	public static String getRootPath() {
		String jarWholePath = FileUtil.class.getProtectionDomain().getCodeSource().getLocation().getFile();
		try {
			jarWholePath = java.net.URLDecoder.decode(jarWholePath, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String jarPath = new File(jarWholePath).getParentFile().getAbsolutePath();
		return jarPath;
	}
}
