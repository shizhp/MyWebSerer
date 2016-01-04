package MyWebServer;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
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
	private PrintStream out;

	public RequestHandler(Request request, Response response) {
		this.request = request;
		this.response = response;
		out = response.getOut();
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
						+ request.getHeader("uri"));
		File file;
		if (request.getHeader("uri") == null) {
			file = new File(HttpServer.serverConfigMap.get("BASIC_ROOT"));
		} else {
			file = new File(HttpServer.serverConfigMap.get("BASIC_ROOT"),
					request.getHeader("uri"));
		}
		if (file.exists()) {
			if (file.isDirectory()) {
				logger.info("预览文件夹:{}", file.getName());
				viewDir(file);
			} else if (file.isFile()) {
				logger.info("预览文件:{}", file.getName());
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
		String uri = request.getHeader("uri");
		int indexOfSlash = 0;
		if (uri != null) {
			indexOfSlash = uri.lastIndexOf('/');
		}

		if (uri != null && indexOfSlash > 0) {
			result.append("<br><a href=\"/"
					+ request.getHeader("uri").substring(0, indexOfSlash)
					+ "\"" + " target=\"view_windows\"" + ">" + "返回上层目录"
					+ "</a><br>");
		} else {
			result.append("<br><a href=\"/"
					+ "\""+ " target=\"view_windows\"" + ">" + "返回上层目录" + "</a><br>");
		}

		for (File childFile : childFiles) {
			String childFilePath = request.getHeader("uri") + File.separator
					+ childFile.getName();

			if (childFile.isDirectory()) {
				result.append("<br><a href=\""
						+ childFilePath + "\"" + " target=\"view_windows\""
						+ ">" + childFile.getName() + "</a><br>");
			} else {
				result.append("<br><a href=\"" 
						+ childFilePath + "\"" + " target=\"view_windows\""
						+ ">" + childFile.getName() + "</a>");
				result.append("&nbsp&nbsp&nbsp<a href=\""
						 + childFilePath + "\""
						+ " download=\"" + childFile.getName()
						+ "\">download</a><br>");
			}
		}
		result.append("</body>");
		result.append("</html>");
		out.println("HTTP/1.1 200 OK");
		out.println("MIME_version:1.0");
		out.println("Content-Type:text/html");
		out.println(("Content-Length:" + result.length()));
		out.println("");
		out.write(result.toString().getBytes());
		out.flush();
	}

	/**
	 * 浏览文本文件模块
	 * 
	 * @param file
	 * @throws Exception
	 */
	public void viewFile(File file) throws Exception {
		logger.info("**文件长度为 {}", file.length());
		String range = request.getHeader("Range");

		String contentType = getContentType(file);
		if (range.equals("all") == true) {
			sendFileResponseHead(file, contentType);
			logger.info("*请求的文件范围为 {} {}", 0, file.length());
			downLoadFile(file);// 初始使用方法
		} else {
			getFileRange();
			sendPartFileResponseHead(file, contentType);
			logger.info("**请求的文件范围为 {} {}", fileStartRange, fileEndRange);
			downLoadPartFile(file);

		}
	}

	private String getContentType(File file) throws FileNotFoundException,
			IOException {
		String fileType = getFileType(file);
		File contentTypeIni = new File(System.getProperty("user.dir"),
				"ContentType.ini");
		Properties ppsContentType = new Properties();
		ppsContentType.load(new FileInputStream(contentTypeIni));
		String contentType = ppsContentType.getProperty(fileType,
				"application/octet-stream");
		return contentType;
	}

	private void sendFileResponseHead(File file, String contentType) {
		out.println("HTTP/1.1 200 OK");
		out.println("MIME_version:1.0");
		out.println("Content-Length:" + file.length());
		out.println("Content_Type:" + contentType + "charset = UTF-8");
		out.println("");

		logger.info("Response {}", "HTTP/1.1 200 OK");
		logger.info("Response {}", "MIME_version:1.0");
		logger.info("Response {}", "Content-Length:" + file.length());
		logger.info("Response {}", "Content_Type:" + contentType
				+ "charset = UTF-8");
	}

	private void sendPartFileResponseHead(File file, String contentType) {
		out.println("HTTP/1.1 206 OK");
		out.println("MIME_version:1.0");
		out.println("Content-Range: bytes " + fileStartRange + '-'
				+ fileEndRange + '/' + file.length());
		out.println("Content-Length: " + (fileEndRange - fileStartRange + 1));
		out.println("Content_Type:" + contentType + "charset = UTF-8");
		out.println("");

		logger.info("Response {}", "HTTP/1.1 206 OK");
		logger.info("Response {}", "MIME_version:1.0");
		logger.info("Response {}", "Content-Range: bytes " + fileStartRange
				+ '-' + fileEndRange + '/' + file.length());
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
			out.write(tmpBuff);
		}
		out.flush();
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
		out.write(buff);
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
		if(indexOfDot != -1){
			return fileName.substring(indexOfDot);
		}
		return ".txt";
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
		out.println("HTTP/1.1 404 OK");
		out.println("MIME_version:1.0");
		out.println("Content-Type:text/html");
		out.println(("Content-Length:" + result.length()));
		out.println("");
		out.write(result.toString().getBytes());
		out.flush();
	}

	private void getFileRange() {
		String range = request.getHeader("Range");
		int indexOfSplit;
		indexOfSplit = range.indexOf('-');
		String startRange = range.substring(0, indexOfSplit);
		String endRange = range.substring(indexOfSplit + 1);
		fileStartRange = Long.parseLong(startRange);
		fileEndRange = Long.parseLong(endRange);
		logger.info("文件开始位置{}", fileStartRange);
		logger.info("文件结束位置{}", fileEndRange);
	}
}
