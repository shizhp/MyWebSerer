package MyWebServer;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 将处理后的信息返回给浏览器
 * 
 * @author shizhp
 * @data 2015年12月24日
 */
public class Response {
	static public Logger logger = LoggerFactory.getLogger(Response.class);
	private PrintStream out;
	private Socket socket;

	public Response(Socket client) throws IOException {
		socket = client;
		out = new PrintStream(socket.getOutputStream());
	}

	public PrintStream getOut() {
		return out;
	}
}
