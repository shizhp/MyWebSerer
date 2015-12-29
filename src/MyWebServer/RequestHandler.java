package MyWebServer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

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
		HttpServer.logger.info("文件路径为 {}", "path:" + HttpServer.getBASIC_ROOT() + "/"
				+ request.getUri());
		File file;
		if (request.getUri() == null) {
			file = new File(HttpServer.getBASIC_ROOT());
		} else {
			file = new File(HttpServer.getBASIC_ROOT(), request.getUri());
		}
		if(file.exists()){
			if (file.isDirectory()) {
				HttpServer.logger.info("预览文件夹");
				viewDir(file);
			} else if (file.isFile()) {
				HttpServer.logger.info("预览文件");
				viewFile(file);
		}		
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
	public void viewDir(File file) throws Exception {
		StringBuilder result = new StringBuilder();
		File[] childFiles = file.listFiles();
		result.append("<html>");
		result.append("<head>");
		result.append("<title>简单web服务器</title>");
		result.append("</head>");
		result.append("<body>");
		result.append("<div align=" + "center" + ">服务器已经成功启动 </div>\n");
		int indexOfSlash = request.getUri().lastIndexOf('/');
		if(indexOfSlash > 0){
			result.append("<br><a href=\"" + HttpServer.HOST
				+ request.getUri().substring(0, indexOfSlash) + "\"" + " target=\"view_windows\""
				+ ">" + "返回上层目录" + "</a><br>");
		}
		else{
			result.append("<br><a href=\"" + HttpServer.HOST + "\"" + " target=\"view_windows\""
					+ ">" + "返回上层目录" + "</a><br>");
		}
		
		for (File childFile : childFiles) {
			String childFilePath = request.getUri() + File.separator
					+ childFile.getName();
			
			if (childFile.isDirectory()) {
				result.append("<br><a href=\"" + HttpServer.HOST
						+ childFilePath + "\"" + " target=\"view_windows\""
						+ ">" + childFile.getName() + "</a><br>");
			} else {
				result.append("<br><a href=\"" + "Http://localhost:8189/"
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
		response.getOut().println("HTTP/1.1 200 OK");
		response.getOut().println("MIME_version:1.0");
		response.getOut().println("Content_Type:text/html");
		response.getOut().println(("Content_Length:"+result.length()));
		response.getOut().println("");
		response.getOut().write(result.toString().getBytes());
		response.getOut().flush();
	}

	/**
	 * 浏览文本文件模块
	 * 
	 * @param file
	 * @throws Exception
	 */
	public void viewFile(File file) throws Exception {
		String fileType = getFileType(file);
		File contentTypeIni = new File(System.getProperty("user.dir") + "\\src\\MyWebServer","ContentType.ini");
		Properties ppsContentType = new Properties();
		ppsContentType.load(new FileInputStream(contentTypeIni));
		String contentType = ppsContentType.getProperty(fileType, "application/octet-stream");
		response.getOut().println("HTTP/1.1 200 OK");
		response.getOut().println("MIME_version:1.0");
		response.getOut().println("Content_Type:" + contentType + "charset = UTF-8");
		response.getOut().println(("Content_Length:"+file.length()));
		response.getOut().println("");
		int readMark;
		FileInputStream fis = new FileInputStream(file);
		ByteArrayOutputStream buff = new ByteArrayOutputStream(4096);
		byte[] tmpbuff = new byte[4096];
		while ((readMark = fis.read(tmpbuff)) != -1) {
			buff.write(tmpbuff, 0, readMark);
		}
		response.getOut().write(buff.toByteArray());
		response.getOut().flush();
		fis.close();
	}

	/**获取请求文件的文件名
	 * @param file
	 * @return
	 */
	private String getFileType(File file) {
		String fileName = file.getName();
		int indexOfDot = fileName.indexOf('.');
		return fileName.substring(indexOfDot);
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
		response.getOut().println("HTTP/1.1 404 OK");
		response.getOut().println("MIME_version:1.0");
		response.getOut().println("Content_Type:text/html");
		response.getOut().println(("Content_Length:"+result.length()));
		response.getOut().println("");
		response.getOut().write(result.toString().getBytes());
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
