package MyWebServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.URLDecoder;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 从浏览器的请求输入流中获取请求字符串
 * 
 * @author shizhp
 * @data 2015年12月24日
 */
public class Request {
	static public Logger logger = LoggerFactory.getLogger(Request.class);
	private String uri;
	private String range;
	private InputStream inputStream = null;
	private StringBuilder requestString = new StringBuilder(4096);
	private Socket socket;
	private HashMap<String, String> requestHeadMap = new HashMap<String, String>();

	public Request(Socket client) throws Exception {
		socket = client;
		inputStream = socket.getInputStream();
	}

	/**
	 * 将请求流转换为字符串
	 * 
	 * @return
	 * @throws IOException
	 */
	public String parseRequest() throws IOException {
		byte[] buffer = new byte[4096];int i = inputStream.read(buffer);
			for (int j = 0; j < i; j++) {
				requestString.append((char) buffer[j]);
			}
//		try {
//			
//		} finally {
//			if(inputStream != null){
//				try{
//					inputStream.close();
//				}catch(Exception e){
//					
//				}
//			}	
//		}
		logger.info("REQUEST {}", requestString.toString());
		getRequestMap(requestString.toString());
		logger.info("GET {}", uri);
		return uri;
	}

	/**
	 * 将请求字符串中的uri解析出来
	 * 
	 * @param requestString
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public void getRequestMap(String requestString)//可以把所有的field字段提取出来
			throws UnsupportedEncodingException {
		int index1, index2;
		index1 = requestString.indexOf(" ");
		if (index1 != -1) {
			index2 = requestString.indexOf(" ", index1 + 1);
			if ((index1 + 1) < index2) {
				uri = requestString.substring(index1 + 2, index2);
				uri = URLDecoder.decode(uri, "utf-8");
				logger.info("请求内容为 {}", uri);
				requestHeadMap.put("uri", uri);
			} else {
				requestHeadMap.put("uri", "/");
			}
			if (index2 != -1 && requestString.indexOf("Range", index2) != -1) {
				int indexOfBytes, indexOfEqual, indexOfEnter;
				indexOfBytes = requestString.indexOf("bytes", index2);
				indexOfEqual = requestString.indexOf('=', indexOfBytes);
				indexOfEnter = requestString.indexOf('\r', indexOfBytes);
				range = requestString.substring(indexOfEqual + 1, indexOfEnter);
				logger.info("bytes开始位置{}", indexOfBytes);
				logger.info("=开始位置{}", indexOfEqual);
				logger.info("enter开始位置{}", indexOfEnter);
				requestHeadMap.put("Range", range);
			} else {
				requestHeadMap.put("Range", "all");
			}
		}
	}

	/**
	 * 获取请求相应字段的值
	 * 
	 * @param field
	 * @return
	 */
	public String getHeader(String field) {
		return requestHeadMap.get(field);
	}
}
