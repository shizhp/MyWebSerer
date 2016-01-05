package MyWebServer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;

public class Util {
	/**获取与文件相对应的Content-Type
	 * @param file
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static String getContentType(File file) throws FileNotFoundException,
			IOException {
		String fileType = Util.getFileType(file);
		File contentTypeIni = new File(System.getProperty("user.dir"),
				"ContentType.ini");
		Properties ppsContentType = new Properties();
		ppsContentType.load(new FileInputStream(contentTypeIni));
		String contentType = ppsContentType.getProperty(fileType,
				"application/octet-stream");
		return contentType;
	}

	/**
	 * 获取请求文件的文件名
	 * @param file
	 * @return
	 */
	public static String getFileType(File file) {
		String fileName = file.getName();
		int indexOfDot = fileName.indexOf('.');
		if (indexOfDot != -1) {
			return fileName.substring(indexOfDot);
		}
		return ".txt";
	}

	/**
	 * 获取断点续传的文件范围
	 * 
	 * @param range
	 * @return
	 */
	public static HashMap<String, String> getFileRange(String range) {
		HashMap<String, String> rangeValue = new HashMap<String, String>();
		int indexOfSplit;
		indexOfSplit = range.indexOf('-');
		String startRange = range.substring(0, indexOfSplit);
		String endRange = range.substring(indexOfSplit + 1);
		rangeValue.put("startRange", startRange);
		rangeValue.put("endRange", endRange);
		return rangeValue;
	}
}
