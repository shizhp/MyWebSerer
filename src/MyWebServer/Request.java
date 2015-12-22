package MyWebServer;

import java.io.IOException;
import java.io.InputStream;

/**解析浏览器请求内容
 * @author shizhp
 * @data 2015年12月21日
 */
public class Request {
	/**解析请求主方法
	 * @param in
	 * @return
	 */
	public String parseRequest(InputStream in){
		String get = null;
		StringBuilder request = new StringBuilder(4096);
		byte[] buffer = new byte[4096];
		try{
			int i = in.read(buffer);
			for(int j = 0; j < i; j++){
				request.append((char)buffer[j]);
			}
			System.out.println(request.toString());
			get = parseUri(request.toString());
			return get;
		}catch(IOException e){
			
		}	
		return get;
	}
	
	/*从请求中解析出Uri
	 * @param RequestString
	 * @return
	 */
	public String parseUri(String requestString){
		int index1, index2;
		index1 = requestString.indexOf(" ");
		if(index1 != -1){
			index2 = requestString.indexOf(" ", index1 + 1);
			if(index1 < index2){
				System.out.println("请求的网页名为：" + requestString.substring(index1 + 2, index2));
				return requestString.substring(index1 + 2, index2);
			}
		}		
		return null;
	}
}
