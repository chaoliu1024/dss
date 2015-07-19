package nuist.qlib.dss.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import net.sf.json.JSONObject;
import nuist.qlib.dss.constant.RoleType;
import nuist.qlib.dss.net.util.NetPropertiesUtil;
import nuist.qlib.dss.net.vo.IPMessageVO;

import org.apache.log4j.Logger;

/**
 * 
 * 接收IP的线程
 * 
 */
public class ReceIP implements Runnable // 接收
{
	private String host;
	private Logger logger;
	private static long[] timer;
	boolean isLink;

	public ReceIP() {
		logger = Logger.getLogger(ReceIP.class.getName());
		this.host = "239.0.0.1";
		timer = new long[10];
		for (int i = 0; i < timer.length; i++) {// 0~3存放artJudge,4~7存放execJudge,8、9存放impJudge
			timer[i] = 0;
		}
		this.isLink = BroadcastIP.getLink();
	}

	public void run() {
		MulticastSocket receSocket = null;
		InetAddress ip = null;
		try {
			receSocket = new MulticastSocket(9999);
			ip = InetAddress.getByName(this.host);
			receSocket.joinGroup(ip); // 加入到广播组
			while (true) {
				// 接收信息
				byte[] data = new byte[256];
				DatagramPacket packet = new DatagramPacket(data, data.length);
				receSocket.receive(packet);

				// 解析接收的信息
				String message = new String(packet.getData(), 0,
						packet.getLength());
				JSONObject jsonObject = JSONObject.fromObject(message);
				IPMessageVO ipMessageVO = (IPMessageVO) JSONObject.toBean(
						jsonObject, IPMessageVO.class);

				// 根据角色更新该角色记录的时间
				RoleType roleType = ipMessageVO.getRoleType();
				if (roleType == null) {
					continue;
				} else {
					if (roleType.isArtJudge()) {// 接受到的为艺术裁判
						int roleIndex = Integer.valueOf((String) message
								.split("/")[0].subSequence(8, 10));// 裁判序号
						timer[roleIndex - 1] = System.currentTimeMillis();
					} else if (roleType.isExecJudge()) {// 接受到的完成裁判
						int roleIndex = Integer.valueOf((String) message
								.split("/")[0].subSequence(9, 11));// 裁判序号
						timer[3 + roleIndex] = System.currentTimeMillis();
					} else if (roleType.isImpJudge()) {// 接受到的舞步裁判
						int roleIndex = Integer.valueOf((String) message
								.split("/")[0].subSequence(8, 10));// 裁判序号
						timer[7 + roleIndex] = System.currentTimeMillis();
					}

					// 将接受的消息存入address.properties文件
					NetPropertiesUtil.saveIPAddress(ipMessageVO);
				}
			}
		} catch (IOException e) {
			logger.error("加入广播组失败!");
			throw new RuntimeException("加入广播组失败!");
		} finally {
			if (receSocket != null) {
				receSocket.close();
				logger.error("多播套接字已关闭！");
				// 重新打开
				try {
					receSocket = new MulticastSocket(9999);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static long[] getTimer() {
		return timer;
	}

	public static void setTimer(long[] timer) {
		ReceIP.timer = timer;
	}
}