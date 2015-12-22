package MyWebServer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * 处理请求模块，格局请求的不同调用不同的处理模块，并动态生成相应的网页
 * 
 * @author shizhp
 * @data 2015年12月21日
 */
public class RequestHandler {
	
	/**处理请求，调用不同的处理模块，返回生成网页的路径
	 * @param get
	 * @return
	 */
	public byte[] requestHandler(String get) {		
		String path;
		System.out.println("get:" + get);
		path = HttpServer.WEB_ROOT + File.separator + get;
		System.out.println("path:" + path);
		File file = null;

		file = new File(path);
		String parentDir = file.getParent();
		String[] fName = null;
		StringBuilder result = new StringBuilder();
		if(file.isDirectory()){
			fName = file.list();
		}
		try {
				result.append("<html>\n");
				result.append("<head>\n");
				result.append("<title>简单Web服务器</title>\n");
				result.append("</head>\n");
				result.append("<body>\n");
				result.append("<div align=" + "center" + ">服务器已经成功运行 </div>\n");
				int i;
				for(i = 0; i < fName.length; i++){
					if(parentDir == "D:\\eclipse\\workspace\\WebServer\\webroot"){
						
						result.append("<br><a href=\"" +fName[i] + "\">" + fName[i] + "</a>\r");
					}
					else{
						
						get.replace('/','\\');
						parentDir = get;
						result.append("<br><a href=\"" +parentDir + "\\" + fName[i] + "\">" + fName[i] + "</a>\r");
					}
					
					System.out.println(parentDir + "/" + fName[i] + ":" + i);
				}				
				result.append("</body>\n");
				result.append("</html>");
		} finally{
			
		}
		return result.toString().getBytes();
	}

}
