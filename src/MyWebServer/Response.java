package MyWebServer;

import java.io.OutputStream;

/**返回处理之后的信息
 * @author shizhp
 * @data 2015年12月23日
 */
public class Response {
	
	private OutputStream out;

	/**调用该输出流
	 * @return
	 */
	public OutputStream getOut() {
		return out;
	}

	/**设置输出流
	 * @param out
	 */
	public void setOut(OutputStream out) {
		this.out = out;
	}
}
