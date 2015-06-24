package nuist.qlib.dss.net;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * 
 * 接收IP的线程
 * 
 */
public class ReceIP implements Runnable // 接收
{
	private MulticastSocket dsock; // 广播套接字
	private String host;
	private Logger logger;
	private Properties pro;

	public ReceIP(MulticastSocket dsock) {
		logger = Logger.getLogger(ReceIP.class.getName());
		this.dsock = dsock;
		this.host = "239.0.0.1";
		pro = new Properties();
	}

	public void run() {
		try {
			InetAddress ip = InetAddress.getByName(this.host);
			dsock.joinGroup(ip); // 加入到广播组
			while (true) {
				byte[] data = new byte[256];

				DatagramPacket packet = new DatagramPacket(data, data.length);
				dsock.receive(packet);
				String message = new String(packet.getData(), 0,
						packet.getLength());
				// 根据角色更新该角色记录的时间
				String role = message.split("/")[0];
				String databaseIp = message.split("/")[1];
				if (role != null && !role.equals("")) {
					if (role.contains("Editor")) {
						String relativelyPath=System.getProperty("user.dir");
						FileInputStream in = new FileInputStream(
								relativelyPath+"\\dataBase.properties");
						pro.load(in);
						in.close();
						pro.setProperty("ip", databaseIp);
						FileOutputStream out = new FileOutputStream(
								relativelyPath+"\\dataBase.properties");
						pro.store(out, "update");
						out.close();
					}
				}
			}
		} catch (Exception e) {
			throw new RuntimeException("接受失败!");
		} finally {
			dsock.close();
		}
	}
}