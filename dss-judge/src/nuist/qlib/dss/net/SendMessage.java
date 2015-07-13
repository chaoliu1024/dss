package nuist.qlib.dss.net;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.Callable;

import nuist.qlib.dss.activity.LoginActivity;
import android.util.Log;

/**
 * 类名：ClientOutputThread 功能：建立主客户端的输出线程类， 向服务器端传送信息
 */
public class SendMessage implements Callable<Integer> {

	// tcp/ip协议
	private static final String TAG = "SendMessage";
	private Socket socket;
	private String ip;
	private String scoreValue;

	/**
	 * 发送分数
	 * @param ip
	 * @param scoreValue
	 * @param subScoreValue
	 */
	public SendMessage(String ip, String scoreValue) {
		this.ip = ip;
		this.scoreValue = scoreValue;
	}

	@Override
	public Integer call() throws Exception {
		// TODO Auto-generated method stub
		try {
			socket = new Socket(ip, 6666);
			OutputStream os = socket.getOutputStream();
			String s = null;
			if (scoreValue.equalsIgnoreCase("")) {
				scoreValue = "0";
			}
			s = LoginActivity.role
					.substring(0, LoginActivity.role.length() - 2)
					+ "/"
					+ LoginActivity.role
							.substring(LoginActivity.role.length() - 2)
					+ "/"
					+ scoreValue;
			
			System.out.println("s:"+s);
			Log.i(TAG, s + ":" + ip);
			os.write(s.getBytes("utf-8"));
			os.flush();
			os.close();
			socket.close();

		} catch (UnknownHostException e) {
			Log.e(TAG, e.getMessage());
			e.printStackTrace();
			return -1;
		} catch (ConnectException e) {
			Log.e(TAG, e.getMessage());
			e.printStackTrace();
			return -1;
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
			e.printStackTrace();
			return -1;
		}
		return 1;
	}
}
