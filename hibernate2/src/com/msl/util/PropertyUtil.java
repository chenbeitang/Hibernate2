package com.msl.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * 获取config.properties配置文件数据工具类
 * 
 * @author czqiang6
 *
 */
public class PropertyUtil {
	private static Properties prop = null;
	static {
		prop=readConfig("jdbc.properties");
	}

	/**
	 * 获取properties文件key对应的value
	 * 
	 * @param key
	 * @return value
	 */
	public static String getProperty(String key) {

		return prop.getProperty(key);

	}
	/**
	 * 根据properties文件返回对应的properties对象
	 * @param path *.properties 文件路径
	 * @return 读取文件后的property对象
	 */
	public static Properties getInstance(String path) {
		Properties prop = new Properties();
		try {
			prop.load(new FileInputStream(new File(path)));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return prop;
	}
	
	public static Properties readConfig(String fileName) {
		File file = new File(FileUtil.getRootPath() + "/"+fileName);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		 return PropertyUtil.getInstance(file.getAbsolutePath());
	}
}
