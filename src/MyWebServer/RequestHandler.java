package MyWebServer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

/**
 * 根据不同的请求内容进行不同的文件处理操作，并调用response类进行输出
 * 
 * @author shizhp
 * @data 2015年12月24日
 */
public class RequestHandler {
	Request request;
	Response response;

	/**
	 * 分析请求的内容，判断是否为文件或文件夹，并调用相应模块进行处理
	 * 
	 * @throws Exception
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
			System.out.println("预览文件");
			viewText(file);
		} else {
			fileNotExit();
		}
	}

	/**
	 * 先是文件夹子目录
	 * 
	 * @param file
	 * @throws Exception
	 */
	public void viewFiles(File file) throws Exception {
		StringBuilder result = new StringBuilder();
		File[] childFiles = file.listFiles();
		result.append("<html>");
		result.append("<head>");
		result.append("<title>简单web服务器</title>");
		result.append("</head>");
		result.append("<body>");
		result.append("<div align=" + "center" + ">服务器已经成功启动 </div>\n");
		for (File childFile : childFiles) {
			String childFilePath = request.getUri() + File.separator
					+ childFile.getName();
			if (childFile.isDirectory()) {
				result.append("<br><a href=\"" + "Http://localhost:8189"
						+ childFilePath + "\"" + " target=\"view_windows\""
						+ ">" + childFile.getName() + "</a>");
				result.append("&nbsp&nbsp&nbsp<a href=\""
						+ "Http://localhost:8189" + childFilePath + "\""
						+ " download=\"" + childFile.getName()
						+ ".zip\">download</a><br>");
			} else {
				result.append("<br><a href=\"" + "Http://localhost:8189"
						+ childFilePath + "\"" + " target=\"view_windows\""
						+ ">" + childFile.getName() + "</a>");
				result.append("&nbsp&nbsp&nbsp<a href=\""
						+ "Http://localhost:8189" + childFilePath + "\""
						+ " download=\"" + childFile.getName()
						+ "\">download</a><br>");
			}
		}
		result.append("</body>");
		result.append("</html>");
		response.getOut().write(result.toString().getBytes("GBK"));
		response.getOut().flush();
	}

	/**
	 * 浏览文本文件模块
	 * 
	 * @param file
	 * @throws Exception
	 */
	public void viewText(File file) throws Exception {
		int readMark;
		FileInputStream fis = new FileInputStream(file);
		byte[] buff = new byte[4096];
		while ((readMark = fis.read(buff)) != -1) {
			response.getOut().write(buff);
		}
		response.getOut().flush();
		fis.close();
	}

	/**
	 * 输入错误路径处理
	 * 
	 * @throws IOException
	 * @throws Exception
	 */
	private void fileNotExit() throws IOException, Exception {
		StringBuilder result = new StringBuilder();
		result.append("<html>");
		result.append("<head>");
		result.append("<title>找不到该文件</title>");
		result.append("</head>");
		result.append("<body>");
		result.append("<div align=" + "center>" + "404找不到指定文件" + "</div>\n");
		result.append("</body>");
		result.append("</html>");
		response.getOut().write(result.toString().getBytes("GBK"));
		response.getOut().flush();
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
