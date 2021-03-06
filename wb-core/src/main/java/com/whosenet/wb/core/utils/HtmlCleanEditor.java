package com.whosenet.wb.core.utils;

import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

import java.beans.PropertyEditorSupport;

/**
 * Utils-HTML清理.
 *
 * @author <a href="http://ruo.whosenet.com">ruo.whosenet.com</>
 * @version 1.0
 */
public class HtmlCleanEditor extends PropertyEditorSupport {

	/** 是否移除两端空白 */
	private boolean trim;

	/** 是否将空转换为null */
	private boolean emptyAsNull;

	/** 白名单 */
	private Whitelist whitelist = Whitelist.none();

	/**
	 * @param trim
	 *            是否移除两端空白
	 * @param emptyAsNull
	 *            是否将空转换为null
	 */
	public HtmlCleanEditor(boolean trim, boolean emptyAsNull) {
		this.trim = trim;
		this.emptyAsNull = emptyAsNull;
	}

	/**
	 * @param trim 是否移除两端空白
	 * @param emptyAsNull 是否将空转换为null
	 * @param whitelist 白名单
	 */
	public HtmlCleanEditor(boolean trim, boolean emptyAsNull, Whitelist whitelist) {
		this.trim = trim;
		this.emptyAsNull = emptyAsNull;
		this.whitelist = whitelist;
	}

	/**
	 * 获取内容
	 * 
	 * @return 内容
	 */
	@Override
	public String getAsText() {
		Object value = getValue();
		return value != null ? value.toString() : "";
	}

	/**
	 * 设置内容
	 * 
	 * @param text 内容
	 */
	@Override
	public void setAsText(String text) {
		if (text != null) {
			String value = trim ? text.trim() : text;
			value = Jsoup.clean(value, whitelist);
			if (emptyAsNull && "".equals(value)) {
				value = null;
			}
			setValue(value);
		} else {
			setValue(null);
		}
	}

}