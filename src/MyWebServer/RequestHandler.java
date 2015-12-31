package MyWebServer;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
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
	long fileStartRange = 0L;
	long fileEndRange = 0L;

	/**
	 * 分析请求的内容，判断是否为文件或文件夹，并调用相应模块进行处理
	 * 
	 * @throws Exception
	 */
	public void requestAnalyse() throws Exception {
		request.parseRequest();
		HttpServer.logger.info("文件路径为 {}", "path:" + HttpServer.getBASIC_ROOT()
				+ "/" + request.getUri());
		File file;
		if (request.getUri() == null) {
			file = new File(HttpServer.getBASIC_ROOT());
		} else {
			file = new File(HttpServer.getBASIC_ROOT(), request.getUri());
		}
		if (file.exists()) {
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
		if (indexOfSlash > 0) {
			result.append("<br><a href=\"" + HttpServer.HOST
					+ request.getUri().substring(0, indexOfSlash) + "\""
					+ " target=\"view_windows\"" + ">" + "返回上层目录" + "</a><br>");
		} else {
			result.append("<br><a href=\"" + HttpServer.HOST + "\""
					+ " target=\"view_windows\"" + ">" + "返回上层目录" + "</a><br>");
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
		HttpServer.logger.info("**文件长度为 {}", file.length());
		int indexOfRange = request.getRequestString().indexOf("Range");
		String fileType = getFileType(file);
		File contentTypeIni = new File(System.getProperty("user.dir")
				+ "\\src\\MyWebServer", "ContentType.ini");
		Properties ppsContentType = new Properties();
		ppsContentType.load(new FileInputStream(contentTypeIni));
		String contentType = ppsContentType.getProperty(fileType,
				"application/octet-stream");

		if (indexOfRange == -1) {
			sendFileResponseHead(file, contentType);
			HttpServer.logger.info("*请求的文件范围为 {} {}", 0, file.length());
			downLoadFile1(file);// 初始使用方法
			// downLoadFile2(file);
			// downLoadFile3(file);
		} else {
			sendPartFileResponseHead(file, contentType);
			HttpServer.logger.info("**请求的文件范围为 {} {}", fileStartRange,
					fileEndRange);
			// downLoadPartFile1(file);
			// downLoadPartFile2(file);
			downLoadPartFile3(file);
		}
	}

	

	private void sendFileResponseHead(File file, String contentType) {
		response.getOut().println("HTTP/1.1 200 OK");
		HttpServer.logger.info("Response {}", "HTTP/1.1 200 OK");
		response.getOut().println("MIME_version:1.0");
		HttpServer.logger.info("Response {}", "MIME_version:1.0");
		response.getOut().println("Content-Length:" + file.length());
		HttpServer.logger
				.info("Response {}", "Content-Length:" + file.length());
		response.getOut().println(
				"Content_Type:" + contentType + "charset = UTF-8");
		HttpServer.logger.info("Response {}", "Content_Type:" + contentType
				+ "charset = UTF-8");
		response.getOut().println("");
	}
	
	private void sendPartFileResponseHead(File file, String contentType) {
		response.getOut().println("HTTP/1.1 206 OK");
		HttpServer.logger.info("Response {}", "HTTP/1.1 206 OK");
		response.getOut().println("MIME_version:1.0");
		HttpServer.logger.info("Response {}", "MIME_version:1.0");
		getFileRange();
		response.getOut().println(
				"Content-Range: bytes " + fileStartRange + '-'
						+ fileEndRange + '/' + file.length());
		HttpServer.logger
				.info("Response {}",
						"Content-Range: bytes " + fileStartRange + '-'
								+ fileEndRange + '/' + file.length());
		response.getOut().println(
				"Content-Length: " + (fileEndRange - fileStartRange + 1));
		HttpServer.logger.info("Response {}", "Content-Length: "
				+ (fileEndRange - fileStartRange + 1));
		response.getOut().println(
				"Content_Type:" + contentType + "charset = UTF-8");
		HttpServer.logger.info("Response {}", "Content_Type:" + contentType
				+ "charset = UTF-8");
		response.getOut().println("");
	}
	
	private void downLoadFile1(File file) throws IOException {
		int readMark;
		FileInputStream fis = new FileInputStream(file);// 老古董方法
		ByteArrayOutputStream buff = new ByteArrayOutputStream(4096);
		byte[] tmpbuff = new byte[4096];
		while ((readMark = fis.read(tmpbuff)) != -1) {
			buff.write(tmpbuff, 0, readMark);
		}
		response.getOut().write(buff.toByteArray());
		response.getOut().flush();
		fis.close();
	}
	
	private void downLoadFile2(File file) throws IOException {
		BufferedInputStream bis = new BufferedInputStream(// 一次性读取到byte数组中
				new FileInputStream(file));
		int intLen = (int) file.length();
		byte[] bytearray = new byte[(int) file.length()];
		int count = 0;
		int n;
		while (count < intLen
				&& (n = bis.read(bytearray, count,
						Math.min(4096, intLen - count))) != -1) {
			count += n;
		}
		response.getOut().write(bytearray);
		response.getOut().flush();
		bis.close();
	}

	private void downLoadFile3(File file) throws IOException {
		int readMark;
		FileInputStream fis = new FileInputStream(file);
		byte[] tmpBuff = new byte[4096];
		while ((readMark = fis.read(tmpBuff)) != -1) {
			response.getOut().write(tmpBuff);
		}
		response.getOut().flush();
		fis.close();
	}

	/* 更低效率读取法，一个字节一个字节的读取，迅雷下载只有几B/S */
	private void downLoadPartFile1(File file) throws IOException {
		int readMark;
		RandomAccessFile ras = new RandomAccessFile(file, "r");
		ByteArrayOutputStream buff = new ByteArrayOutputStream(4096);
		byte tmpBuff;
		int count = 0;
		ras.seek(fileStartRange);
		while ((tmpBuff = ras.readByte()) != -1 && count <= fileEndRange) {
			// buff.write(tmpBuff, (int)fileStartRange, (int)fileEndRange);
			response.getOut().write(tmpBuff);
			count++;
		}
		// response.getOut().write(buff.toByteArray());
		response.getOut().flush();
		ras.close();
	}

	private void downLoadPartFile2(File file) throws IOException {
		/* 较高效读取法，值读取range范围之内的内容 */
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

	private void downLoadPartFile3(File file) throws IOException {
		/* 低效读取法，将文件全部读取到内存中,修改response报头后迅雷支持断点续传 */
		BufferedInputStream bis = new BufferedInputStream(new FileInputStream(
				file));
		int intLen = (int) file.length();
		byte[] byteArray = new byte[intLen];
		int count = 0;
		int n;
		while (count < intLen
				&& (n = bis.read(byteArray, count,
						Math.min(4096, intLen - count))) != -1) {
			count += n;
		}
		// ByteArrayOutputStream buff = new ByteArrayOutputStream(4096);
		// buff.write(byteArray, (int) fileStartRange, (int) (fileEndRange
		// - fileStartRange + 1));
		response.getOut().write(byteArray, (int) fileStartRange,
				(int) (fileEndRange - fileStartRange + 1));
		response.getOut().flush();
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
			HttpServer.logger.info("bytes开始位置{}", indexOfBytes);
			HttpServer.logger.info("=开始位置{}", indexOfEqual);
			HttpServer.logger.info("-开始位置{}", indexOfSplit);
			HttpServer.logger.info("enter开始位置{}", indexOfEnter);
			HttpServer.logger.info("文件开始位置{}", fileStartRange);
			fileEndRange = Long.parseLong(endRange);
			HttpServer.logger.info("文件结束位置{}", fileEndRange);
		}
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
