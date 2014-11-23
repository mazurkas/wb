/*
 * Copyright 2005-2013 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package com.whosenet.wb.portal;

/**
 * 公共参数
 * 
 * @author albertruo@gmail.com
 * @version 1.0
 */
public final class CommonAttributes {

	/** 日期格式配比 */
	public static final String[] DATE_PATTERNS = new String[] { "yyyy", "yyyy-MM", "yyyyMM", "yyyy/MM", "yyyy-MM-dd", "yyyyMMdd", "yyyy/MM/dd", "yyyy-MM-dd HH:mm:ss", "yyyyMMddHHmmss", "yyyy/MM/dd HH:mm:ss" };

	/** shopxx.xml文件路径 */
	public static final String APP_XML_PATH = "/kyzny.xml";

	/** shopxx.properties文件路径 */
	public static final String APP_PROPERTIES_PATH = "/kyzny.properties";

	/**
	 * 不可实例化
	 */
	private CommonAttributes() {
	}

}