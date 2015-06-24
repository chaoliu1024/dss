package nuist.qlib.dss.net;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

/**
 * 
 * 广播自己的IP地址
 * 
 */
public class BroadcastIP implements Runnable // 发送
{
	private Shell shell;
	private MulticastSocket dsock;
	private int port;
	private String host;
	private String data;
	private InetAddress addr;
	private Logger logger;
	private static boolean isLink = true;// 是否联网

	public BroadcastIP(MulticastSocket dsock, Shell shell) {
		this.shell = shell;
		this.dsock = dsock;
		this.port = 9999; // 广播时局域网中成员接收的端口号
		this.host = "239.0.0.1"; // 局域网广播地址
		logger = Logger.getLogger(BroadcastIP.class.getName());
	}

	public void run() {
		try {
			while (true) {
				addr = InetAddress.getLocalHost(); // 本地IP
				// System.out.println("发送："+addr.getHostAddress()+"");
				data = "Editor/" + InetAddress.getLocalHost().getHostAddress(); // 编辑员IP
				if (addr != null && !addr.getHostAddress().equals("127.0.0.1")) {
					DatagramPacket dataPack = // 数据打包
					new DatagramPacket(data.getBytes(), data.length(),
							InetAddress.getByName(host), // 广播
							port // 目标端口
					);
					dsock.send(dataPack);
					if (!isLink) {
						Display.getDefault().syncExec(new Runnable() { // 非SWT线程无法修改SWT界面，必须调用SWT线程进行调用
									public void run() {
										MessageBox netBox = new MessageBox(
												shell, SWT.None);
										netBox.setMessage("网络已连接,请继续使用");
										netBox.open();
									}
								});
					}
					isLink = true;
				} else {
					// System.out.println("网络未连接");
					if (isLink) {
						Display.getDefault().syncExec(new Runnable() { // 非SWT线程无法修改SWT界面，必须调用SWT线程进行调用
									public void run() {
										MessageBox netBox = new MessageBox(
												shell, SWT.None);
										netBox.setMessage("网络未连接,请检查网络连接");
										netBox.open();
									}
								});
					}
					isLink = false;
				}
				Thread.sleep(5 * 1000); // 睡眠30秒
			}
		} catch (UnknownHostException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new RuntimeException("发送失败!");
		} finally {
			dsock.close();
		}
	}

	public static boolean getLink() {
		return isLink;
	}

	public static void setLink(boolean isLink) {
		BroadcastIP.isLink = isLink;
	}

}