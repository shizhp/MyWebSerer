package MyWebServer;

import java.io.OutputStream;

/**���ش���֮�����Ϣ
 * @author shizhp
 * @data 2015��12��23��
 */
public class Response {
	
	private OutputStream out;

	/**���ø������
	 * @return
	 */
	public OutputStream getOut() {
		return out;
	}

	/**���������
	 * @param out
	 */
	public void setOut(OutputStream out) {
		this.out = out;
	}
}
