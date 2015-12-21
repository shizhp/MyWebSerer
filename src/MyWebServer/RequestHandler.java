package MyWebServer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * ��������ģ�飬�������Ĳ�ͬ���ò�ͬ�Ĵ���ģ�飬����̬������Ӧ����ҳ
 * 
 * @author shizhp
 * @data 2015��12��21��
 */
public class RequestHandler {
	
	/**�������󣬵��ò�ͬ�Ĵ���ģ�飬����������ҳ��·��
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
			sp.println("<title>��Web������</title>");
			sp.println("</head>");
			sp.println("<body>");
			sp.println("<div align=" + "center" + ">�������Ѿ��ɹ����� </div>");
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
