package com.msl.util;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import javax.swing.JOptionPane;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DBConnUtil {
	private static Connection conn = null;
	private static final Logger logger = LogManager.getLogger();

	/**
	 * 返回数据库连接
	 * 
	 * @return
	 */
	public static Connection getServerConn() {
		if (!(conn instanceof Object)) {
			initConn();
		}
		return conn;
	}

	/**
	 * 初始化数据库连接
	 */
	private static void initConn() {
		 Properties properties = readConfig("jdbc.properties");
//		Properties properties = readClassPath("jdbc.properties");
		String dbType = properties.getProperty("jdbc.type");
		// 公用的数据库参数
		String hostip = properties.getProperty("jdbc.hostip");
		String dbname = properties.getProperty("jdbc.dbname");
		String dbport = properties.getProperty("jdbc.port");
		String username = properties.getProperty("jdbc.username");
		String password = properties.getProperty("jdbc.password");
		String driverName = null;
		String url = null;
		// 如果是微软的sqlserver数据库
		if ("MSSQL".equals(dbType.toUpperCase())) {
			driverName = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
			url = "jdbc:sqlserver://" + hostip + ":" + dbport + ";DatabaseName=" + dbname + ";";
			String ssl = properties.getProperty("ssl.enable");
			if ("1".equals(ssl)) {
				url += "encrypt=true;trustServerCertificate=true";
			}
		} else if ("MYSQL".equals(dbType.toUpperCase())) {// 如果是MySQL数据库
			driverName = "com.mysql.jdbc.Driver";
			url = "jdbc:mysql://" + hostip + ":" + dbport + "/" + dbname
					+ "?useUnicode=true&characterEncoding=UTF8&zeroDateTimeBehavior=convertToNull&transformedBitIsBoolean=true&useOldAliasMetadataBehavior=true";
		} else if ("ORACLE".equals(dbType.toUpperCase())) {
			driverName = "oracle.jdbc.driver.OracleDriver";
			url = "jdbc:oracle:thin:@" + hostip + ":" + dbport + ":" + dbname;
		} else {
			logger.error("未指定数据库类型或参数不正确,数据库连接失败！");
			return;
		}
		try {
			logger.info("Connecting to dbserver url[" + url + "]...");
			Class.forName(driverName);
			conn = DriverManager.getConnection(url, username, password);
			logger.info("Connect to dbserver success...");
		} catch (ClassNotFoundException | SQLException e) {
			logger.error("connect to dbserver error:" + e.toString());
		}
	}

	/**
	 * 从项目或包的绝对路径下读取外部文件,读取不到文件则从classpath下读取
	 * 
	 * @param fileName
	 * @return
	 */
	@SuppressWarnings("unused")
	private static Properties readConfig(String fileName) {
		File file = new File(FileUtil.getRootPath() + "/" + fileName);
		if (!file.exists()) {
			return readClassPath(fileName);
		}
		return PropertyUtil.getInstance(file.getAbsolutePath());
	}

	/**
	 * 从classpath下读取properties文件
	 * 
	 * @param fileName
	 * @return
	 */
	private static Properties readClassPath(String fileName) {
		Properties properties = new Properties();
		try {
			properties.load(DBConnUtil.class.getClassLoader().getResourceAsStream(fileName));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return properties;
	}

}
