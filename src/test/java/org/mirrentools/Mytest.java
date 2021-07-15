package org.mirrentools;

import java.net.HttpURLConnection;
import java.net.URL;

public class Mytest {
	public static void main(String[] args)throws Exception {
		URL http = new URL("http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4");
		HttpURLConnection conn = (HttpURLConnection) http.openConnection();
		System.out.println(conn.getContentLength());
		System.out.println(conn.getContentType());
	}
}
