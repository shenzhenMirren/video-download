package com.szmirren.entity;

import java.util.concurrent.atomic.AtomicInteger;

import javafx.scene.control.Label;

/**
 * 下载文件的列表
 * 
 * @author <a href="https://mirrentools.org">Mirren</a>
 *
 */
public class DownloadItem {
	/** id */
	private String id;
	/** 下载路径 */
	private String url;
	/** 下载进度 */
	private Label urlLabel = new Label();
	/** 下载进度 */
	private Label prog = new Label();
	/** 下载状态 */
	private Label state = new Label();
	/** 下载失败的次数 */
	private AtomicInteger errorCount = new AtomicInteger(0);

	public Label getState() {
		return state;
	}

	public void setState(Label state) {
		this.state = state;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Label getUrlLabel() {
		return urlLabel;
	}

	public void setUrlLabel(Label urlLabel) {
		this.urlLabel = urlLabel;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Label getProg() {
		return prog;
	}

	public void setProg(Label prog) {
		this.prog = prog;
	}

	public int getErrorCount() {
		return errorCount.get();
	}

	/**
	 * 失败次数加1并返回加一后的结果
	 * 
	 * @return
	 */
	public int errorCountIAG() {
		return this.errorCount.incrementAndGet();
	}

	public void setErrorCount(AtomicInteger errorCount) {
		this.errorCount = errorCount;
	}

	@Override
	public String toString() {
		return "DownloadItem [id=" + id + ", url=" + url + ", urlLabel=" + urlLabel + ", prog=" + prog + ", state=" + state
				+ "]";
	}

}
