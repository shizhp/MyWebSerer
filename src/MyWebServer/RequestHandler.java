package MyWebServer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * 处理请求模块，格局请求的不同调用不同的处理模块，并动态生成相应的网页
 * 
 * @author shizhp
 * @data 2015年12月21日
 */
public class RequestHandler {
	
	/**处理请求，调用不同的处理模块，返回生成网页的路径
	 * @param get
	 * @return
	 */
	public String requestHandler(String get) {
		String path;
		System.out.println("get:" + get);
		path = HttpServer.WEB_ROOT + File.separator + get;
		System.out.println("path:" + path);
		File file = new File(path);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			FileOutputStream fos;
			fos = new FileOutputStream(file);
			PrintStream sp = new PrintStream(fos);
			sp.println("<html>");
			sp.println("<head>");
			sp.println("<title>简单Web服务器</title>");
			sp.println("</head>");
			sp.println("<body>");
			sp.println("<div align=" + "center" + ">服务器已经成功运行 </div>");
			sp.println("</body>");
			sp.println("</html>");
			sp.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return path;
	}

}
