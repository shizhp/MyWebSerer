package MyWebServer;

import java.io.IOException;
import java.io.InputStream;

/**�����������������
 * @author shizhp
 * @data 2015��12��21��
 */
public class Request {
	/**��������������
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
	
	/*�������н�����Uri
	 * @param RequestString
	 * @return
	 */
	public String parseUri(String requestString){
		int index1, index2;
		index1 = requestString.indexOf(" ");
		if(index1 != -1){
			index2 = requestString.indexOf(" ", index1 + 1);
			if(index1 < index2){
				System.out.println("�������ҳ��Ϊ��" + requestString.substring(index1 + 2, index2));
				return requestString.substring(index1 + 2, index2);
			}
		}		
		return null;
	}
}
