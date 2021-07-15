package com.szmirren.controller;

/**
 * 页面枚举类
 * 
 * @author <a href="http://szmirren.com">Mirren</a>
 *
 */
public enum FXMLPage {
	/** 数据库连接页面 */
	CONNECTION("FXML/Connection.fxml");
	
	private String fxml;

	FXMLPage(String fxml) {
		this.fxml = fxml;
	}

	public String getFxml() {
		return this.fxml;
	}

}
