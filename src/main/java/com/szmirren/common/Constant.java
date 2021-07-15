package com.szmirren.common;

/**
 * 工具需要用到的常量词
 * 
 * @author <a href="http://szmirren.com">Mirren</a>
 *
 */
public interface Constant {
	/** 未下载状态 */
	public final static String STATE_DOWN_WAIT_START= "等待开始";
	/** 未下载状态 */
	public final static String STATE_DOWN_WAIT = "未下载";
	/** 下载中状态 */
	public final static String STATE_DOWN_ING = "下载中";
	/** 完成状态 */
	public final static String STATE_DOWN_OK = "下载完成";
	/** 失败状态 */
	public final static String STATE_DOWN_ERROR = "下载失败,点击重试!";
}
