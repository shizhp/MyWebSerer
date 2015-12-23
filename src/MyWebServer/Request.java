package MyWebServer;

import java.io.IOException;
import java.io.InputStream;

/**
 * 解析浏览器请求内容
 * 
 * @author shizhp
 * @data 2015年12月21日
 */
public class Request {
	/**
	 * 解析请求主方法
	 * @param in
	 * @return
	 */
	private String uri;
	private InputStream inputStream;

	public InputStream getInputStream() {
		return inputStream;
	}

	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String parseRequest() throws IOException {
		StringBuilder request = new StringBuilder(4096);
		byte[] buffer = new byte[4096];

		int i = inputStream.read(buffer);
		for (int j = 0; j < i; j++) {
			request.append((char) buffer[j]);
		}
		System.out.println(request.toString());
		uri = parseUri(request.toString());
		System.out.println(uri);
		return uri;

	}

	/*
	 * 从请求中解析出Uri
	 * 
	 * @param RequestString
	 * 
	 * @return
	 */
	public String parseUri(String requestString) {
		int index1, index2;
		index1 = requestString.indexOf(" ");
		if (index1 != -1) {
			index2 = requestString.indexOf(" ", index1 + 1);
			if ((index1 + 1) < index2) {
				System.out.println("请求的网页名为：" +requestString.substring(index1 + 2, index2));
				return requestString.substring(index1 + 1, index2);
				
				
			}
		}
		return "/";
	}
}
