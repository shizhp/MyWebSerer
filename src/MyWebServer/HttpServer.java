package MyWebServer;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * web服务器，实现用户查看服务器的文件，预览文本文件以及图片
 * 
 * @author shizhp
 * @data 2015年12月21日
 */
public class HttpServer {
	private Logger logger = LoggerFactory.getLogger(HttpServer.class);
	public static HashMap<String, String> serverConfigMap;

	/**
	 * 构造函数，初始化服务器配置文件
	 * 
	 * @throws IOException
	 * @throws IOException
	 */
	@SuppressWarnings("rawtypes")
	private HttpServer() throws IOException, IOException {
		Properties pps = new Properties();
		InputStream in = null;
		serverConfigMap = new HashMap<String, String>();
		try {
			in = new FileInputStream("config.ini");
			pps.load(in);
			Enumeration en = pps.propertyNames();
			while (en.hasMoreElements()) {
				String key = (String) en.nextElement();
				String Property = pps.getProperty(key);
				serverConfigMap.put(key, Property);
			}
		} finally {
			if (in != null) {
				try {
					in.close();
				} finally {

				}
			}

		}
	}

	/**
	 * 服务器启动程序，要注意关闭socket，不然浏览器会一直处于接收数据状态
	 * 
	 * @throws Exception
	 */
	public void startServer() throws Exception {
		// getConfig();
		logger.info("文件根目录BASIC_ROOT： {}", serverConfigMap.get("BASIC_ROOT"));
		logger.info("端口号iPORT: {}", serverConfigMap.get("iPORT"));
		logger.info("服务器主机号HOST: {}", serverConfigMap.get("HOST"));
		ServerSocket server;
		server = new ServerSocket(
				Integer.parseInt(serverConfigMap.get("iPORT")));
		Socket client = null;
		int i = 1;
		ThreadPoolExecutor executor = new ThreadPoolExecutor(5, 10, 10,
				TimeUnit.MINUTES, new ArrayBlockingQueue<Runnable>(5));
		while (i < 1000) {
			client = server.accept();
			ConnectionThread connectionThread = new ConnectionThread(client, i);
			executor.execute(connectionThread);
			i++;
		}
		server.close();
	}

	/* ConnnectionThread类完成与一个Web浏览器的通信 */
	public class ConnectionThread extends Thread {
		private Socket client; // 连接Web浏览器的socket字
		private int counter; // 计数器

		public ConnectionThread(Socket cl, int c) {
			client = cl;
			counter = c;
		}

		public void run() // 线程体
		{
			try {
				String destIP = client.getInetAddress().toString(); // 客户机IP地址
				int destport = client.getPort(); // 客户机端口号
				logger.info("Connection {}:connected to{}  on port {} . ",
						counter, destIP, destport);
				Request request = new Request(client);
				Response response = new Response(client);
				RequestHandler handler = new RequestHandler(request, response);
				handler.requestAnalyse();
				client.close();
			} catch (IOException e) {
				logger.error("Exception: {}", e);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 程序入口
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		HttpServer httpServer = new HttpServer();
		httpServer.startServer();
	}
}
