package MyWebServer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

public class Response {
	private OutputStream out;
	public Response(OutputStream out){
		this.out = out;
	}
	/**动态生成网页返回
	 * @param path
	 * @throws Exception
	 */
	public void sendResource(byte[] result) throws Exception {
		
		out.write(result);
	}
	public void sendResource1(String path) throws Exception {
		File file = null;
		FileInputStream fis = null;
		file = new File(path);
		byte[] buffer = new byte[4096];
		try {
			if (file.exists()) {
				fis = new FileInputStream(file);
				int i;
				i = fis.read(buffer, 0, 4096);
				while(i != -1){
					out.write(buffer, 0, i);
					i = fis.read(buffer, 0, 4096);
				}
				System.out.println("发送完毕");
			}
		} finally{
			
		}
	}

}
