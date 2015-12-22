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
 * web����������������Ҫ������������������Լ����ܿͻ������󣬵��ø����ͻ��˶Կͻ��˵�������д���
 * 
 * @author shizhp
 * @data 2015��12��21��
 */
public class HttpServer {
	public static String BASIC_ROOT;/* Ĭ���ļ����·�� */
	private int iPort;/* �˿ں� */
	public static String WEB_ROOT = System.getProperty("user.dir")
			+ File.separator + "webroot";/* ��̬���ɵ���ҳ��ŵ�λ�� */

	/**
	 * �������ļ��ж�ȡ������ò�����������
	 * 
	 * @throws Exception
	 */
	public void startServer() throws Exception {
		getConfig();
		System.out.println("Ĭ���ļ����·��Ϊ��" + BASIC_ROOT);
		System.out.println("Ĭ�϶˿ں�Ϊ��" + iPort);
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
	 * ���������ļ�����ȡĬ�ϸ�·������ȡ�˿�
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
				throw new Exception("�����ļ�������");
			} else {
				BASIC_ROOT = strValue;
			}
			strKey = (String) enumer.nextElement();
			strValue = ppsIni.getProperty(strKey);
			if (strValue.equals("") == true) {
				throw new Exception("�˿ں�δ����");
			} else {
				iPort = Integer.parseInt(strValue);
			}

		} finally {

		}
	}

	/**
	 * ���������
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		HttpServer httpServer = new HttpServer();
		httpServer.startServer();
	}

}
