package MyWebServer;

import java.io.PrintStream;

/**将处理后的信息返回给浏览器
 * @author shizhp
 * @data 2015年12月24日
 */
public class Response {
	private PrintStream out;
	public PrintStream getOut() {
		return out;
	}
	public void setOut(PrintStream out) {
		this.out = out;
	}
}
