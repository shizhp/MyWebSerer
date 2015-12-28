package MyWebServer;

import java.io.OutputStream;

/**将处理后的信息返回给浏览器
 * @author shizhp
 * @data 2015年12月24日
 */
public class Response {
	private OutputStream out;
	public OutputStream getOut() {
		return out;
	}
	public void setOut(OutputStream out) {
		this.out = out;
	}
}
