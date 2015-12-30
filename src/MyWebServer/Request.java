package MyWebServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;


/**从浏览器的请求输入流中获取请求字符串
 * @author shizhp
 * @data 2015年12月24日
 */
public class Request {
	private String uri;
	private InputStream inputStream;
	private StringBuilder requestString = new StringBuilder(4096);
	
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

	public String getRequestString() {
		return requestString.toString();
	}
	/**将请求流转换为字符串
	 * @return
	 * @throws IOException
	 */
	public String parseRequest() throws IOException {
		byte[] buffer = new byte[4096];
		int i = inputStream.read(buffer);
		for (int j = 0; j < i; j++) {
			requestString.append((char) buffer[j]);
		}
//		System.out.println(requestString.toString());
		HttpServer.logger.info("REQUEST {}", requestString.toString());
		uri = parseUri(requestString.toString());
		HttpServer.logger.info("GET {}", uri);
//		System.out.println(uri);
		return uri;
	}

	/**将请求字符串中的uri解析出来
	 * @param requestString
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	public String parseUri(String requestString) throws UnsupportedEncodingException {
		int index1, index2;
		index1 = requestString.indexOf(" ");
		if (index1 != -1) {
			index2 = requestString.indexOf(" ", index1 + 1);
			if ((index1 + 1) < index2) {	
				String uri = requestString.substring(index1 + 2, index2);
				uri = URLDecoder.decode(uri, "utf-8");
				HttpServer.logger.info("请求内容为 {}", uri);
				return uri;	
			}
		}
		return "/";
	}
}
