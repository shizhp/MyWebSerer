package MyWebServer;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 根据不同的请求内容进行不同的文件处理操作，并调用response类进行输出
 * 
 * @author shizhp
 * @data 2015年12月24日
 */
public class RequestHandler {
	static public Logger logger = LoggerFactory.getLogger(RequestHandler.class);
	private Request request;
	private Response response;
	long fileStartRange = 0L;
	long fileEndRange = 0L;

	public RequestHandler(Request request, Response response){
		this.request = request;
		this.response = response;
	}
	/**
	 * 分析请求的内容，判断是否为文件或文件夹，并调用相应模块进行处理
	 * 
	 * @throws Exception
	 */
	public void requestAnalyse() throws Exception {
		request.parseRequest();
		logger.info("文件路径为 {}",
				"path:" + HttpServer.serverConfigMap.get("BASIC_ROOT") + "/"
						+ request.getUri());
		File file;
		if (request.getUri() == null) {
			file = new File(HttpServer.serverConfigMap.get("BASIC_ROOT"));
		} else {
			file = new File(HttpServer.serverConfigMap.get("BASIC_ROOT"),
					request.getUri());
		}
		if (file.exists()) {
			if (file.isDirectory()) {
				logger.info("预览文件夹:{}");
				viewDir(file);
			} else if (file.isFile()) {
				logger.info("预览文件");
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
		if (indexOfSlash > 0) {
			result.append("<br><a href=\""
					+ HttpServer.serverConfigMap.get("HOST")
					+ request.getUri().substring(0, indexOfSlash) + "\""
					+ " target=\"view_windows\"" + ">" + "返回上层目录" + "</a><br>");
		} else {
			result.append("<br><a href=\""
					+ HttpServer.serverConfigMap.get("HOST") + "\""
					+ " target=\"view_windows\"" + ">" + "返回上层目录" + "</a><br>");
		}

		for (File childFile : childFiles) {
			String childFilePath = request.getUri() + File.separator
					+ childFile.getName();

			if (childFile.isDirectory()) {
				result.append("<br><a href=\""
						+ HttpServer.serverConfigMap.get("HOST")
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
		response.getOut().println("Content-Type:text/html");
		response.getOut().println(("Content-Length:" + result.length()));
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
		logger.info("**文件长度为 {}", file.length());
		int indexOfRange = request.getRequestString().indexOf("Range");
		
		String contentType = getContentType(file);
		if (indexOfRange == -1) {
			sendFileResponseHead(file, contentType);
			logger.info("*请求的文件范围为 {} {}", 0, file.length());
			downLoadFile(file);// 初始使用方法
		} else {
			sendPartFileResponseHead(file, contentType);
			logger.info("**请求的文件范围为 {} {}", fileStartRange,
					fileEndRange);
			downLoadPartFile(file);

		}
	}

	private String getContentType(File file) throws FileNotFoundException, IOException {
		String fileType = getFileType(file);
		File contentTypeIni = new File(System.getProperty("user.dir"), "ContentType.ini");
		Properties ppsContentType = new Properties();
		ppsContentType.load(new FileInputStream(contentTypeIni));
		String contentType = ppsContentType.getProperty(fileType,
				"application/octet-stream");
		return contentType;
	}

	private void sendFileResponseHead(File file, String contentType) {
		response.getOut().println("HTTP/1.1 200 OK");
		response.getOut().println("MIME_version:1.0");
		response.getOut().println("Content-Length:" + file.length());
		response.getOut().println(
				"Content_Type:" + contentType + "charset = UTF-8");
		response.getOut().println("");

		logger.info("Response {}", "HTTP/1.1 200 OK");
		logger.info("Response {}", "MIME_version:1.0");
		logger.info("Response {}", "Content-Length:" + file.length());
		logger.info("Response {}", "Content_Type:" + contentType
				+ "charset = UTF-8");
	}

	private void sendPartFileResponseHead(File file, String contentType) {
		getFileRange();
		response.getOut().println("HTTP/1.1 206 OK");
		response.getOut().println("MIME_version:1.0");
		response.getOut().println(
				"Content-Range: bytes " + fileStartRange + '-' + fileEndRange
						+ '/' + file.length());
		response.getOut().println(
				"Content-Length: " + (fileEndRange - fileStartRange + 1));
		response.getOut().println(
				"Content_Type:" + contentType + "charset = UTF-8");
		response.getOut().println("");

		logger.info("Response {}", "HTTP/1.1 206 OK");
		logger.info("Response {}", "MIME_version:1.0");
		logger.info("Response {}", "Content-Range: bytes "
				+ fileStartRange + '-' + fileEndRange + '/' + file.length());
		logger.info("Response {}", "Content-Length: "
				+ (fileEndRange - fileStartRange + 1));
		logger.info("Response {}", "Content_Type:" + contentType
				+ "charset = UTF-8");
	}

	private void downLoadFile(File file) throws IOException {
		int readMark;
		FileInputStream fis = new FileInputStream(file);
		byte[] tmpBuff = new byte[4096];
		while ((readMark = fis.read(tmpBuff)) != -1) {
			response.getOut().write(tmpBuff);
		}
		response.getOut().flush();
		fis.close();
	}

	/* 较高效读取法，值读取range范围之内的内容 */
	private void downLoadPartFile(File file) throws IOException {
		BufferedInputStream bis = new BufferedInputStream(new FileInputStream(
				file));
		int filePartLength = (int) (fileEndRange - fileStartRange + 1);
		bis.skip(fileStartRange);
		byte[] buff = new byte[filePartLength];
		int count = 0;
		int n;
		while (count < filePartLength
				&& (n = bis.read(buff, count,
						Math.min(4096, filePartLength - count))) != -1) {
			count += n;
		}
		response.getOut().write(buff);
		bis.close();
	}

	/**
	 * 获取请求文件的文件名
	 * 
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
		response.getOut().println("Content-Type:text/html");
		response.getOut().println(("Content-Length:" + result.length()));
		response.getOut().println("");
		response.getOut().write(result.toString().getBytes());
		response.getOut().flush();
	}

	private void getFileRange() {
		String requestString = request.getRequestString();
		int indexOfFirstSpace, indexOfSecondSpace;
		int indexOfBytes, indexOfEqual, indexOfSplit, indexOfEnter;
		// int indexOfStartRange, indexOfEndRange;
		indexOfFirstSpace = requestString.indexOf(' ');
		if (indexOfFirstSpace != -1) {
			indexOfSecondSpace = requestString.indexOf(' ',
					indexOfFirstSpace + 1);
			indexOfBytes = requestString.indexOf("bytes", indexOfSecondSpace);
			indexOfEqual = requestString.indexOf('=', indexOfBytes);
			indexOfEnter = requestString.indexOf('\r', indexOfBytes);
			indexOfSplit = requestString.indexOf('-', indexOfBytes);
			String startRange = requestString.substring(indexOfEqual + 1,
					indexOfSplit);
			String endRange = requestString.substring(indexOfSplit + 1,
					indexOfEnter);
			fileStartRange = Long.parseLong(startRange);
			logger.info("bytes开始位置{}", indexOfBytes);
			logger.info("=开始位置{}", indexOfEqual);
			logger.info("-开始位置{}", indexOfSplit);
			logger.info("enter开始位置{}", indexOfEnter);
			logger.info("文件开始位置{}", fileStartRange);
			fileEndRange = Long.parseLong(endRange);
			logger.info("文件结束位置{}", fileEndRange);
		}
	}
}
