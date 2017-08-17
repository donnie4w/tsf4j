package com.tsf;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * <p>@File:tsf: com.tsf.log :Logger.java
 * <p>@Date:2017年8月6日
 * <p>@Copyright (c) 2017, donnie4w@gmail.com All Rights Reserved.
 * <p>@Author: dong
 * <p>@Desc:
 */
public class Logger {

	java.util.logging.Logger logger;

	private Logger(String cn) {
		logger = java.util.logging.Logger.getLogger(cn);
	}

	public static Logger getLogger() {
		return new Logger(java.lang.Thread.currentThread().getStackTrace()[2].getClassName());
	}

	public void info(Object... args) {
		logger.info(objs2str(args));
	}

	public void info(Object arg, Throwable e) {
		logger.info(objs2str(String.valueOf(arg), e));
	}

	public void severe(Object... args) {
		logger.severe(objs2str(args));
	}

	public void severe(Object arg, Throwable e) {
		logger.severe(objs2str(String.valueOf(arg), e));
	}

	private static String objs2str(Object... args) {
		try {
			if (args != null) {
				StringBuilder sb = new StringBuilder();
				for (Object o : args) {
					if (o instanceof Throwable) {
						ByteArrayOutputStream baos = new ByteArrayOutputStream();
						((Throwable) o).printStackTrace(new PrintStream(baos));
						String s = new String(baos.toByteArray());
						baos.close();
						sb.append(s).append(" ");
					} else {
						sb.append(String.valueOf(o)).append(" ");
					}
				}
				return sb.toString();
			}
		} catch (Exception e) {
		}
		return "";
	}
}
