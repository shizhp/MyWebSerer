package MyWebServer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Enumeration;
import java.util.Properties;

/**
 * web服务器主函数，主要负责服务器的启动，以及接受客户端请求，调用各个客户端对客户端的请求进行处理。
 * 
 * @author shizhp
 * @data 2015年12月21日
 */
public class HttpServer {
	public static String BASIC_ROOT;/* 默认文件存放路径 */
	private int iPort;/* 端口号 */
	public static String WEB_ROOT = System.getProperty("user.dir")
			+ File.separator + "webroot";/* 动态生成的网页存放的位置 */

	/**
	 * 从配置文件中读取相关配置并启动服务器
	 * 
	 * @throws Exception
	 */
	public void startServer() throws Exception {
		getConfig();
		System.out.println("默认文件存放路径为：" + BASIC_ROOT);
		System.out.println("默认端口号为：" + iPort);
		ServerSocket server;
		try {
			server = new ServerSocket(iPort);
			Socket socket;
			InputStream in;
			OutputStream out;
			while (true) {
				socket = server.accept();
				in = socket.getInputStream();
				out = socket.getOutputStream();
				Request request = new Request();
				String get = request.parseRequest(in);
				RequestHandler handler = new RequestHandler();
				Response response = new Response(out);
				response.sendResource(handler.requestHandler(get));
			}
//			server.close();
		} finally {
//			server.close();
		}
	}

	/**
	 * 解析配置文件，获取默认根路径，获取端口
	 * 
	 * @throws Exception
	 */
	public void getConfig() throws Exception {
		File iniFile = new File("config.ini");
		Properties ppsIni = new Properties();// FileInputStream(iniFile);

		try {
			ppsIni.load(new FileInputStream(iniFile));
			Enumeration enumer = ppsIni.propertyNames();
			String strKey = (String) enumer.nextElement();
			String strValue = ppsIni.getProperty(strKey);
			if (strValue.equals("") == true) {
				throw new Exception("配置文件不存在");
			} else {
				BASIC_ROOT = strValue;
			}
			strKey = (String) enumer.nextElement();
			strValue = ppsIni.getProperty(strKey);
			if (strValue.equals("") == true) {
				throw new Exception("端口号未设置");
			} else {
				iPort = Integer.parseInt(strValue);
			}

		} finally {

		}
	}

	/**
	 * 主程序入口
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		HttpServer httpServer = new HttpServer();
		httpServer.startServer();
	}

}
