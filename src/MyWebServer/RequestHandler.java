package MyWebServer;

import java.io.File;
import java.io.FileInputStream;
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
	Request request;
	Response response;

	/**
	 * �������󣬵��ò�ͬ�Ĵ���ģ�飬����������ҳ��·��
	 * 
	 * @param get
	 * @return
	 */
	public void requestAnalyse() throws Exception {
		request.parseRequest();
		System.out.println("path" + HttpServer.getBASIC_ROOT() + "/"
				+ request.getUri());
		File file;
		if (request.getUri() == null) {
			file = new File(HttpServer.getBASIC_ROOT());
		} else {
			file = new File(HttpServer.getBASIC_ROOT(), request.getUri());
		}

		if (file.isDirectory()) {
			System.out.println(file.getPath());
			viewFiles(file);
		} else if (file.isFile()) {
			System.out.println("Ԥ���ļ�");
			String fileName = file.getName();
			int indexOfLastDot = fileName.lastIndexOf(".");
			String fileType = fileName.substring(indexOfLastDot);
			System.out.println(fileType);
			if (fileType.equals(".jpg") || fileType.equals(".txt")) {
				System.out.println(".txt");
				viewText(file);
			}
			if (fileType.equals(".")) {
				System.out.println("Ԥ��ͼƬ");
				viewPicture(file);
			}

		}
	}

	/**
	 * ��ʾ�ļ���Ŀ¼
	 * 
	 * @param file
	 * @throws Exception
	 */
	public void viewFiles(File file) throws Exception {
		StringBuilder result = new StringBuilder();
		if (file.isDirectory()) {
			File[] childFiles = file.listFiles();
			result.append("<html>");
			result.append("<head>");
			result.append("<title>��Web������</title>");
			result.append("</head>");
			result.append("<body>");
			result.append("<div align=" + "center"
					+ ">�������Ѿ��ɹ����� </div>\n");
			for (File childFile : childFiles) {
				String childFilePath = request.getUri() + File.separator
						+ childFile.getName();
				result.append("<br><a href=\"" + "Http://localhost:8189"
						+ childFilePath + "\"" + " target=\"view_windows\""
						+ ">" + childFile.getName() + "</a><br>");
			}
			result.append("</body>");
			result.append("</html>");

		}
		response.getOut().write(result.toString().getBytes());
		response.getOut().flush();
	}

	/**
	 * ��ʾ�ı��ļ�
	 * 
	 * @param file
	 * @throws Exception
	 */
	public void viewText(File file) throws Exception {
		StringBuilder result = new StringBuilder();
		StringBuilder result1 = new StringBuilder();
		// result.append("<html>");
		// result.append("<head>");
		// result.append("<title>��Web������</title>");
		// result.append("</head>");
		// result.append("<body>");
		// result.append("<div align=" + "center" + ">" + request.getUri()
		// + "</div>");
		// result.append("<p>");
		//
		// result.append("</p>");
		// result1.append("</body>");
		// result1.append("</html>");

		response.getOut().write(result.toString().getBytes());
		int readMark;
		FileInputStream fis = new FileInputStream(file);
		byte[] buff = new byte[4096];
		while ((readMark = fis.read(buff)) != -1) {
			response.getOut().write(buff);
		}
		fis.close();
		response.getOut().write(result1.toString().getBytes());
	}

	/**
	 * Ԥ��ͼƬ
	 * 
	 * @param file
	 * @throws IOException
	 */
	private void viewPicture(File file) throws IOException {
		StringBuilder result = new StringBuilder();
		result.append("<html>");
		result.append("<head>");
		result.append("<title>��Web������</title>");
		result.append("</head>");
		result.append("<body>");
		result.append("<div align=" + "center" + ">" + request.getUri()
				+ "</div>");
		StringBuilder filePath = new StringBuilder();
		filePath.append(request.getUri());// "Http://localhost:8189" +

		result.append("<img scr=\"" + filePath
				+ "\" width=\"165\" height=\"60\"" + "alt=\"" + file.getName()
				+ "\"/>");
		result.append("</body>");
		result.append("</html>");
		response.getOut().write(result.toString().getBytes());
	}

	public Response getResponse() {
		return response;
	}

	public void setResponse(Response response) {
		this.response = response;
	}

	public Request getRequest() {
		return request;
	}

	public void setRequest(Request request) {
		this.request = request;
	}
}
