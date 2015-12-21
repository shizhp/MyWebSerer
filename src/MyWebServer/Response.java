package MyWebServer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;

public class Response {
	private OutputStream out;
	public Response(OutputStream out){
		this.out = out;
	}
	public void sendResource(String path) throws Exception {
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
				System.out.println("·¢ËÍÍê±Ï");
			}
		} finally{
			
		}
	}

}
