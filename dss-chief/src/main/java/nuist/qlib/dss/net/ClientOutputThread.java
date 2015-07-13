package nuist.qlib.dss.net;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.Callable;

import net.sf.json.JSONObject;
import nuist.qlib.dss.net.util.NetPropertiesUtil;
import nuist.qlib.dss.net.vo.AllScoreMessageVO;
import nuist.qlib.dss.net.vo.CommandMessageVO;
import nuist.qlib.dss.net.vo.MatchInfoMessageVO;

import org.apache.log4j.Logger;

/**
 * 类名：ClientOutputThread 功能：建立主客户端的输出线程类， 向服务器端传送信息
 */
public class ClientOutputThread implements Callable<Integer> {

	private Socket socket;
	private String ip;
	private String s;
	private Logger logger = Logger
			.getLogger(ClientOutputThread.class.getName());

	/* 给高级裁判组发送所有打分信息 */
	public ClientOutputThread(AllScoreMessageVO allScoreMessageVO) {
		this.ip = allScoreMessageVO.getTargetIp();
		JSONObject jsonObject = JSONObject.fromObject(allScoreMessageVO);
		this.s = jsonObject == null ? null : jsonObject.toString();
	}

	/* 给高级裁判组和裁判发送比赛信息 */
	public ClientOutputThread(MatchInfoMessageVO matchInfoMessageVO) {
		this.ip = matchInfoMessageVO.getTargetIp();
		JSONObject jsonObject = JSONObject.fromObject(matchInfoMessageVO);
		this.s = jsonObject == null ? null : jsonObject.toString();
	}

	public ClientOutputThread(CommandMessageVO commandMessageVO) { // 发送调分指令
		this.ip = commandMessageVO.getTargetIp();
		JSONObject jsonObject = JSONObject.fromObject(commandMessageVO);
		this.s = jsonObject == null ? null : jsonObject.toString();
	}

	public Integer call() {

		try {
			socket = new Socket(ip, 6666);
			OutputStream os = socket.getOutputStream();
			os.write(s.getBytes("utf-8"));
			os.flush();
			os.close();
			socket.close();
		} catch (UnknownHostException | ConnectException e) {
			try {
				NetPropertiesUtil.removeIPAddress(ip);
			} catch (IOException e1) {
				return -1;
			}
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
