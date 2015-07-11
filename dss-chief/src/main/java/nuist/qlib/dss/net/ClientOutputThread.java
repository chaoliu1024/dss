package nuist.qlib.dss.net;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;

/**
 * 类名：ClientOutputThread 功能：建立主客户端的输出线程类， 向服务器端传送信息
 */
public class ClientOutputThread implements Callable<Integer> {

	private Socket socket;
	private String ip;
	private String s;
	private Logger logger = Logger.getLogger(ClientOutputThread.class.getName());

	/*给裁判长发送所有打分信息*/
	public ClientOutputThread(String ip, String artScore1,
			String artScore2, String artScore3, String artScore4,
			String artTotalScore, String completionScore1,
			String completionScore2, String completionScore3,
			String completionScore4, String completionTotalScore,
			String difficultScore, String difficultSubScore,
			String deduction_score, String total) { // 发送打分信息

		this.ip = ip;
		this.s = "all" + "/" + artScore1 + "/" + artScore2 + "/" + artScore3
				+ "/" + artScore4 + "/" + artTotalScore + "/"
				+ completionScore1 + "/" + completionScore2 + "/"
				+ completionScore3 + "/" + completionScore4 + "/"
				+ completionTotalScore + "/" + difficultScore + "/"
				+ difficultSubScore + "/" + deduction_score + "/" + total; 
	}
	
	/*给裁判长和裁判发送队伍信息*/
	public ClientOutputThread(String ip, String matchUnit, String matchCategory) {
		this.ip = ip;
		this.s = "infor1" + "/" + matchUnit + "/" + matchCategory + "/"; 
	}

	/*给裁判长和裁判发送比赛信息*/
	public ClientOutputThread(String ip, String matchUnit,
			String matchCategory, String matchName) {
		this.ip = ip;
		this.s = "infor2" + "/" + matchUnit + "/" + matchCategory + "/"
				+ matchName + "/"; 
	}
	
	public ClientOutputThread(String ip, String command) { // 发送调分指令
		
		this.ip = ip;
		this.s = "Command" + "/" + command;
	}
	
	public ClientOutputThread(String ip) { // 发送队伍信息
		
		this.ip = ip;
		this.s = "infor3" + "/";
	}

	public Integer call() {

		try {
			socket = new Socket(ip, 6666);
			OutputStream os = socket.getOutputStream();
			os.write(s.getBytes("utf-8"));
			os.flush();
			os.close();
			socket.close();
		} catch (UnknownHostException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
			return -1;
		} catch (ConnectException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
			return -1;
		} catch (IOException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
			return -1;
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
			return -1;
		}
		return 1;
	}
}
