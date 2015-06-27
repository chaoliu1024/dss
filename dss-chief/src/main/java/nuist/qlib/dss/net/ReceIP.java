package nuist.qlib.dss.net;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * 接收IP的线程
 * 
 */
public class ReceIP implements Runnable {
	private MulticastSocket dsock; // 广播套接字
	private String host;
	private Logger logger = LoggerFactory.getLogger(ReceIP.class.getName());;
	private String path;
	private static long[] timer;
	boolean isLink;

	public ReceIP(MulticastSocket dsock) {
		this.dsock = dsock;
		this.host = "239.0.0.1";
		// 获取工程目录
		String relativelyPath = System.getProperty("user.dir");
		this.path = relativelyPath + "\\Address.txt";
		timer = new long[10];
		// 0~3存放artJudge,4~7存放execJudge,8、9存放impJudge
		for (int i = 0; i < timer.length; i++) {
			timer[i] = 0;
		}
		this.isLink = BroadcastIP.getLink();
	}

	public void run() {
		InetAddress ip;
		try {
			ip = InetAddress.getByName(this.host);
			dsock.joinGroup(ip); // 加入到广播组
			while (true) {
				byte[] data = new byte[256];

				DatagramPacket packet = new DatagramPacket(data, data.length);

				dsock.receive(packet);
				String message = new String(packet.getData(), 0,
						packet.getLength());
				// 根据角色更新该角色记录的时间
				String role = message.split("/")[0];
				if (role != null && !role.equals("")) {
					if (role.startsWith("art")) {// 接受到的为艺术裁判
						int roleIndex = Integer.valueOf((String) message
								.split("/")[0].subSequence(8, 10));// 裁判序号
						timer[roleIndex - 1] = System.currentTimeMillis();
					} else if (role.startsWith("exec")) {// 接受到的完成裁判
						int roleIndex = Integer.valueOf((String) message
								.split("/")[0].subSequence(9, 11));// 裁判序号
						timer[3 + roleIndex] = System.currentTimeMillis();
					} else if (role.startsWith("imp")) {// 接受到的印象裁判
						int roleIndex = Integer.valueOf((String) message
								.split("/")[0].subSequence(8, 10));// 裁判序号
						timer[7 + roleIndex] = System.currentTimeMillis();
					}
				}
				// 将接受的消息存入Address.txt文件
				try {
					FileWriter fw = new FileWriter(path, true);
					PrintWriter pw = new PrintWriter(fw);
					FileReader fr = new FileReader(path);
					BufferedReader br = new BufferedReader(fr);

					while (true) {
						String messageTemp = br.readLine();
						if (messageTemp == null) {
							pw.println(message);
							break;
						} else if (messageTemp.equalsIgnoreCase(message))
							break;
						else {
							if (messageTemp.split("/")[0]
									.equalsIgnoreCase(message.split("/")[0])
									|| messageTemp.split("/")[1]
											.equalsIgnoreCase(message
													.split("/")[1])) { // 若身份一样ip不一样或ip一样身份不一样则更新
								fw.close();
								pw.close();
								fw = new FileWriter(path);
								fw.write("");
								pw = new PrintWriter(fw);
								break;
								// pw.println(message); // 写入新的IP
							} else
								continue;
						}

					}
					pw.close();
					fw.close();
					br.close();
					fr.close();
				} catch (IOException e) {
					logger.error(e.getMessage());
					e.printStackTrace();
				}
			}
		} catch (UnknownHostException e1) {
			logger.error("IP为未知地址!");
			throw new RuntimeException("IP为未知地址!");
		} catch (IOException e1) {
			logger.error("加入广播组失败!");
			throw new RuntimeException("加入广播组失败!");
		} finally {
			dsock.close();
			logger.error("多播套接字已关闭！");
		}
	}

	public static long[] getTimer() {
		return timer;
	}

	public static void setTimer(long[] timer) {
		ReceIP.timer = timer;
	}
}