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
 * web服务器，实现用户查看服务器的文件，预览文本文件以及图片
 * 
 * @author shizhp
 * @data 2015年12月21日
 */
public class HttpServer {
	public static String BASIC_ROOT;/*服务器根目录 */
	private static int iPort;/*端口号*/
	/**
	 * 服务器启动程序，要注意关闭socket，不然浏览器会一直处于接收数据状态
	 * @throws Exception
	 */
	@SuppressWarnings("resource")
	public void startServer() throws Exception {
		getConfig();
		System.out.println("文件根目录为" + BASIC_ROOT);
		System.out.println("端口号为" + iPort);
		ServerSocket server;
		server = new ServerSocket(iPort);
		while (true) {
			Socket socket = new Socket();
			InputStream in;
			OutputStream out;
			socket = server.accept();
			in = socket.getInputStream();
			out = socket.getOutputStream();
			Request request = new Request();
			request.setInputStream(in);			
			RequestHandler handler = new RequestHandler();
			Response response = new Response();
			response.setOut(out);
			handler.setRequest(request);			
			handler.setResponse(response);	
			handler.requestAnalyse();
			socket.close();
		}
	}

	/**
	 * 获取服务器根目录以及端口号的配置信息
	 * @throws Exception
	 */
	public void getConfig() throws Exception {
		File iniFile = new File(System.getProperty("user.dir"), "config.ini");
		Properties ppsIni = new Properties();// FileInputStream(iniFile);

		try {
			ppsIni.load(new FileInputStream(iniFile));
			BASIC_ROOT = ppsIni.getProperty("BASIC_ROOT");
			if (BASIC_ROOT.equals("") == true) {
				throw new Exception("路径未设置");
			}
			iPort = Integer.parseInt(ppsIni.getProperty("iPORT"));
			if (ppsIni.getProperty("iPORT").equals("") == true) {
				throw new Exception("端口号未设置");
			}
		} finally {

		}
	}

	public static String getBASIC_ROOT() {
		return BASIC_ROOT;
	}

	/**从配置文件中获取根目录
	 * @throws Exception
	 */
	public static void setBASIC_ROOT() throws Exception {
		File iniFile = new File(System.getProperty("user.dir"), "config.ini");
		Properties ppsIni = new Properties();
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
		} finally {

		}
	}

	/**
	 * 程序入口
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		HttpServer httpServer = new HttpServer();
		httpServer.startServer();
	}

}
