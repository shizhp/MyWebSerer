package MyWebServer;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Enumeration;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * web服务器，实现用户查看服务器的文件，预览文本文件以及图片
 * 
 * @author shizhp
 * @data 2015年12月21日
 */
public class HttpServer {
	static public Logger logger = LoggerFactory.getLogger(HttpServer.class);
	public static String BASIC_ROOT;/* 服务器根目录 */
	private static int iPort;/* 端口号 */
	public static String HOST;

	/**
	 * 服务器启动程序，要注意关闭socket，不然浏览器会一直处于接收数据状态
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("resource")
	public void startServer() throws Exception {
		getConfig();
		// System.out.println("文件根目录为" + BASIC_ROOT);
		// System.out.println("端口号为" + iPort);
		logger.info("文件根目录为 {}", BASIC_ROOT);
		logger.info("端口号为 {}", iPort);
		logger.info("服务器主机号为 {}", HOST);
		ServerSocket server;
		server = new ServerSocket(iPort);
		while (true) {
			Socket socket = new Socket();
			InputStream in;
			PrintStream out;
			socket = server.accept();
			in = socket.getInputStream();
			out = new PrintStream(socket.getOutputStream());
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

	/* ConnnectionThread类完成与一个Web浏览器的通信 */

	class ConnectionThread extends Thread {
		public Socket client; // 连接Web浏览器的socket字
		public int counter; // 计数器
		public ConnectionThread(Socket cl, int c) {
			client = cl;
			counter = c;
		}
		@SuppressWarnings("deprecation")
		public void run() // 线程体
		{
			try {
				String destIP = client.getInetAddress().toString(); // 客户机IP地址
				int destport = client.getPort(); // 客户机端口号
				logger.info("Connection " + counter + ":connected to "
						+ destIP + " on port " + destport + ".");
				InputStream in;
				PrintStream out;
				in = client.getInputStream();
				out = new PrintStream(client.getOutputStream());
				Request request = new Request();
				request.setInputStream(in);
				RequestHandler handler = new RequestHandler();
				Response response = new Response();
				response.setOut(out);
				handler.setRequest(request);
				handler.setResponse(response);
				handler.requestAnalyse();
				long m1 = 1;
				while (m1 < 11100000) {
					m1++;
				} // 延时
				client.close();
			} catch (IOException e) {
				System.out.println("Exception:" + e);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 获取服务器根目录以及端口号的配置信息
	 * 
	 * @throws Exception
	 */
	public void getConfig() throws Exception {
		File iniFile = new File(System.getProperty("user.dir")
				+ "\\src\\MyWebServer", "config.ini");
		Properties ppsIni = new Properties();// FileInputStream(iniFile);
		ppsIni.load(new FileInputStream(iniFile));
		BASIC_ROOT = ppsIni.getProperty("BASIC_ROOT");
		if (BASIC_ROOT.equals("") == true) {
			logger.info("error {}", "路径未设置");
			throw new Exception("路径未设置");
		}
		iPort = Integer.parseInt(ppsIni.getProperty("iPORT"));
		if (ppsIni.getProperty("iPORT").equals("") == true) {
			logger.info("error {}", "端口号未设置");
			throw new Exception("端口号未设置");
		}
		HOST = ppsIni.getProperty("HOST");
		if (HOST.equals("") == true) {
			logger.info("error {}", "服务器地址未设置");
			throw new Exception("服务器地址未设置");
		}
	}

	public static String getBASIC_ROOT() {
		return BASIC_ROOT;
	}

	/**
	 * 程序入口
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		HttpServer httpServer = new HttpServer();
		httpServer.getConfig();
		logger.info("文件根目录为 {}", BASIC_ROOT);
		logger.info("端口号为 {}", iPort);
		logger.info("服务器主机号为 {}", HOST);
		ServerSocket server;
		server = new ServerSocket(iPort);
		Socket client = null;
		int i = 1;
		while (true) {
			client = server.accept();
			ConnectionThread connectionThread = httpServer.new ConnectionThread(client, i);
			connectionThread.start();
			i++;
		}
		
	}

}
