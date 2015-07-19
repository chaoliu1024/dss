package nuist.qlib.dss.net;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

import net.sf.json.JSONObject;
import nuist.qlib.dss.constant.RoleType;
import nuist.qlib.dss.net.util.IPUtil;
import nuist.qlib.dss.net.vo.IPMessageVO;

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
	private int port;
	private String host;
	private Logger logger;
	private static boolean isLink = true;// 是否联网

	public BroadcastIP(Shell shell) {
		this.shell = shell;
		this.port = 9999; // 广播时局域网中成员接收的端口号
		this.host = "239.0.0.1"; // 局域网广播地址
		logger = Logger.getLogger(BroadcastIP.class.getName());
	}

	public void run() {
		MulticastSocket sendSocket = null;
		try {
			sendSocket = new MulticastSocket(9998);
			String localAddress = IPUtil.getLocalAddress(); // 获取本地IP
			if (localAddress != null && !"127.0.0.1".equals(localAddress)) {
				// 设置IP消息
				IPMessageVO ipMessageVO = new IPMessageVO();
				ipMessageVO.setRoleType(RoleType.EDITOR);
				ipMessageVO.setOriginalIp(localAddress);
				// 对象转json
				JSONObject jsonObject = JSONObject.fromObject(ipMessageVO);
				// json转字符串
				String message = jsonObject.toString();

				// 数据打包
				DatagramPacket dataPack = new DatagramPacket(
						message.getBytes(), message.length(),
						InetAddress.getByName(host), // 广播
						port // 目标端口
				);
				sendSocket.send(dataPack);
				if (!isLink) {
					Display.getDefault().syncExec(new Runnable() { // 非SWT线程无法修改SWT界面，必须调用SWT线程进行调用
								public void run() {
									MessageBox netBox = new MessageBox(shell,
											SWT.None);
									netBox.setMessage("网络已连接,请继续使用");
									netBox.open();
								}
							});
				}
				isLink = true;
			} else {
				if (isLink) {
					Display.getDefault().syncExec(new Runnable() { // 非SWT线程无法修改SWT界面，必须调用SWT线程进行调用
								public void run() {
									MessageBox netBox = new MessageBox(shell,
											SWT.None);
									netBox.setMessage("网络未连接,请检查网络连接");
									netBox.open();
								}
							});
				}
				isLink = false;
			}
		} catch (UnknownHostException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new RuntimeException("发送失败!");
		} finally {
			if (sendSocket != null) {
				sendSocket.close();
			}
		}
	}

	public static boolean getLink() {
		return isLink;
	}

	public static void setLink(boolean isLink) {
		BroadcastIP.isLink = isLink;
	}

}