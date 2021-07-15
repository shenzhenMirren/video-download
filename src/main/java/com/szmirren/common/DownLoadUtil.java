package com.szmirren.common;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class DownLoadUtil {
	public DownLoadUtil() {
		super();
	}

	/**
	 * 将图片保存到本地
	 * 
	 * @param imgUrl
	 *          远程图片的地址http
	 * @param localPath
	 *          本地的路径
	 * @throws Exception
	 */
	public static void getRemoteToLocalImage(String imgUrl, Path localPath) throws Exception {
		getRemoteToLocalImage(imgUrl, localPath, null);
	}
	/**
	 * 将图片保存到本地
	 * 
	 * @param imgUrl
	 *          远程图片的地址http
	 * @param localPath
	 *          本地的路径
	 * @throws Exception
	 */
	public static void getRemoteToLocalImage(String imgUrl, Path localPath, Map<String, String> header) throws Exception {
		URL url = new URL(imgUrl);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		if (header != null) {
			header.entrySet().forEach(h -> {
				conn.addRequestProperty(h.getKey(), h.getValue());
			});
		}
		try (InputStream bis = conn.getInputStream()) {
			System.out.println("Connection succeed");
			// 1K的数据缓冲
			byte[] bs = new byte[1048576];
			// 读取到的数据长度
			int len;
			if (Files.notExists(localPath)) {
				Files.createFile(localPath);
			}
			try (OutputStream os = Files.newOutputStream(localPath)) {
				// 输出的文件流
				while ((len = bis.read(bs)) != -1) {
					os.write(bs, 0, len);
				}
				os.flush();
			} catch (Exception e) {
				e.printStackTrace();
			} ;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 将图片保存到本地
	 * 
	 * @param imgUrl
	 *          远程图片的地址http
	 * @param localPath
	 *          本地的路径
	 * @throws Exception
	 */
	public static void getRemoteToLocalMp3(String imgUrl, Path localPath) throws Exception {
		getRemoteToLocalMp3(imgUrl, localPath, null);
	}
	/**
	 * 将图片保存到本地
	 * 
	 * @param imgUrl
	 *          远程图片的地址http
	 * @param localPath
	 *          本地的路径
	 * @throws Exception
	 */
	public static void getRemoteToLocalMp3(String imgUrl, Path localPath, Map<String, String> header) throws Exception {
		URL urlfile = new URL(imgUrl);
		URLConnection con = urlfile.openConnection();
		con.setConnectTimeout(10000);
		if (header != null) {
			header.entrySet().forEach(h -> {
				con.addRequestProperty(h.getKey(), h.getValue());
			});
		}
		try (InputStream bis = con.getInputStream()) {
			System.out.println("Connection succeed");
			// 1K的数据缓冲
			byte[] bs = new byte[1048576];
			// 读取到的数据长度
			int len;
			if (Files.notExists(localPath)) {
				Files.createFile(localPath);
			}
			try (OutputStream os = Files.newOutputStream(localPath)) {
				// 输出的文件流
				while ((len = bis.read(bs)) != -1) {
					os.write(bs, 0, len);
				}
				os.flush();
			} catch (Exception e) {
				e.printStackTrace();
			} ;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 将HTML保存到本地
	 * 
	 * @param htmlUrl
	 * @param cookie
	 * @param 文件种子
	 * @param startPath
	 * @throws Exception
	 */
	public static void getRemoteToLocalHtml(String htmlUrl, String cookie, String index, String startPath) throws Exception {
		HttpClient httpclient = HttpClientBuilder.create().build();
		RequestConfig config = RequestConfig.custom().setRedirectsEnabled(true).build();
		HttpGet get = new HttpGet(htmlUrl);
		get.setConfig(config);
		get.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64;rv:56.0) Gecko/20100101 Firefox/56.0");
		get.addHeader("Accept", "*/*");
		get.addHeader("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");
		get.addHeader("Accept-Encoding", "gzip, deflate, br");
		get.addHeader("Referer", htmlUrl);
		get.addHeader("Cookie", cookie);
		HttpResponse response = httpclient.execute(get);
		String html = EntityUtils.toString(response.getEntity());
		Document document = Jsoup.parse(html);
		Elements mpvoice = document.getElementsByTag("mpvoice");
		int mpcount = 1;
		for (Element element : mpvoice) {
			String attr = element.attr("voice_encode_fileid");
			Element parent = element.parent();
			getRemoteToLocalMp3("https://res.wx.qq.com/voice/getvoice?mediaid=" + attr, Paths.get(startPath + "html/mp3/" + index + "-" + mpcount + ".mp3"));
			parent.append("<audio src=\"mp3/" + index + "-" + mpcount + ".mp3" + "\" controls=\"controls\">当前浏览器不支持,请用新版浏览器</audio>");
			mpcount++;
		}
		Elements imgs = document.getElementsByTag("img");
		int imgCount = 1;
		for (Element img : imgs) {
			String attr = img.attr("data-src");
			if (attr != null && attr.startsWith("http") && attr.lastIndexOf("=") != -1) {
				String imgName = index + "-" + imgCount + "." + attr.substring(attr.lastIndexOf("=") + 1);
				getRemoteToLocalImage(attr, Paths.get(startPath + "html/images/" + imgName));
				imgCount++;
				img.attr("src", "images/" + imgName);
			} else if (attr.endsWith(".png?")) {
				String imgName = index + "-" + imgCount + ".png";
				getRemoteToLocalImage(attr, Paths.get(startPath + "html/images/" + imgName));
				imgCount++;
				img.attr("src", "images/" + imgName);
			}
		}
		Files.write(Paths.get(startPath + "html/" + index + ".html"), document.toString().getBytes(), StandardOpenOption.CREATE);

	}

}
