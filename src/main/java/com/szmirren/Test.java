package com.szmirren;

import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.net.URL;

public class Test {
	public static void main(String[] args) throws Exception {
		// ShadowsocksR (80单端口)
		// 信息
		// 二维码
		// 链接
		// JSON
		// 服务器地址：gkdcm1.gkdcn.net
		// 服务器端口：22965
		// 加密方式：chacha20-ietf
		// 密码：m2KSOD
		// 协议：auth_aes128_md5
		// 协议参数：2378:vZQUFM
		// 混淆：tls1.2_ticket_auth
		// 混淆参数：.microsoft.com
		// Proxy proxy = new Proxy(Proxy.Type.HTTP, new
		// InetSocketAddress("gkdcm1.gkdcn.net", 22965));
		Proxy proxy = new Proxy(Type.HTTP, new InetSocketAddress("127.0.0.1", 7890));
		System.out.println("开始请求...");
		URL http = new URL(
				"https://scontent.cdninstagram.com/v/t50.2886-16/129789775_181710156951911_8440416152912807793_n.mp4?efg=eyJ2ZW5jb2RlX3RhZyI6InZ0c192b2RfdXJsZ2VuLjcyMC5mZWVkLmRlZmF1bHQiLCJxZV9ncm91cHMiOiJbXCJpZ193ZWJfZGVsaXZlcnlfdnRzX290ZlwiXSJ9&_nc_ht=scontent.cdninstagram.com&_nc_cat=111&_nc_ohc=BDI_n0kv1vwAX-eFtHn&edm=APU89FABAAAA&vs=17852205656348383_516970325&_nc_vs=HBksFQAYJEdFOXZ2QWRuY1Z1eFE2VUFBSEZIMXBiRllTSjFia1lMQUFBRhUAAsgBABUAGCRHQkh5cmdlZjRwQTJoWHdBQUVuZWpNdjZEbVlZYmtZTEFBQUYVAgLIAQAoABgAGwGIB3VzZV9vaWwBMBUAACa%2B6PTvkJ%2B2PxUCKAJDMywXQBOZmZmZmZoYEmRhc2hfYmFzZWxpbmVfMV92MREAdeoHAA%3D%3D&_nc_rid=75351ad71f&ccb=7-4&oe=60F06FB4&oh=ef7c6bc85aacef03095bfc453b869c7a&_nc_sid=86f79a");
		System.out.println("打开链接...");
		HttpURLConnection conn = (HttpURLConnection) http.openConnection(proxy);
		System.out.println("打开链接成功!");
		conn.setConnectTimeout(1000);
		int code = conn.getResponseCode();
		System.out.println(code);
		System.out.println(conn.getResponseMessage());

	}
}
