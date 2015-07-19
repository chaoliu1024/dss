package nuist.qlib.dss.net.util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;

import nuist.qlib.dss.constant.RoleType;
import nuist.qlib.dss.net.vo.IPMessageVO;

import org.apache.commons.lang.StringUtils;

public class NetPropertiesUtil {

	private static final String NET_PROPERTIES_FILE = "/conf/net.properties";
	private static final String IP_ADDRESS_FILE = "/conf/address.properties";

	/**
	 * 获取广播地址（IP和端口号）
	 * 
	 * @return
	 * @throws IOException
	 * @since DSS 1.0
	 */
	public static InetSocketAddress getInetSocketAddress() throws IOException {
		Properties pros = new Properties();
		InputStream in = NetPropertiesUtil.class
				.getResourceAsStream(NET_PROPERTIES_FILE);
		pros.load(in);
		String ip = pros.getProperty("IPBroadcaster.ip");
		String port = pros.getProperty("IPBroadcaster.port");
		return new InetSocketAddress(ip, Integer.parseInt(port));
	}

	/**
	 * 获取广播地址（端口号）
	 * 
	 * @return
	 * @throws IOException
	 * @since DSS 1.0
	 */
	public static InetSocketAddress getInetSocketPort() throws IOException {
		Properties pros = new Properties();
		InputStream in = NetPropertiesUtil.class
				.getResourceAsStream(NET_PROPERTIES_FILE);
		pros.load(in);
		String port = pros.getProperty("IPBroadcaster.port");
		return new InetSocketAddress(Integer.parseInt(port));
	}

	/**
	 * 保存角色和对应的IP
	 * 
	 * @param msg
	 * @throws IOException
	 * @since DSS 1.0
	 */
	public static void saveIPAddress(IPMessageVO msg) throws IOException {
		RoleType role = msg.getRoleType();
		String ip = msg.getOriginalIp();
		if (role == null || StringUtils.isBlank(ip)) {
			return;
		} else {
			Properties pros = new Properties();
			InputStream in = NetPropertiesUtil.class
					.getResourceAsStream(IP_ADDRESS_FILE);
			pros.load(in);
			in.close();

			pros.setProperty(role.getKeyWord(), ip);
			OutputStream out = new FileOutputStream(IP_ADDRESS_FILE);
			pros.store(out, "address");
			out.close();
		}
	}

	/**
	 * 获取接收队伍信息的ip
	 * 
	 * @return
	 * @throws IOException
	 * @since DSS 1.0
	 */
	@SuppressWarnings("rawtypes")
	public static List<String> getTeamReceiver() throws IOException {
		Properties pros = new Properties();
		InputStream in = NetPropertiesUtil.class
				.getResourceAsStream(IP_ADDRESS_FILE);
		pros.load(in);
		in.close();

		List<String> teamReceiver = new ArrayList<String>();
		for (Entry entry : pros.entrySet()) {
			if (RoleType.EDITOR.getKeyWord().equals(entry.getKey())) {
				continue;
			}
			teamReceiver.add((String) entry.getValue());
		}

		return teamReceiver;
	}

	/**
	 * 获取高级裁判组的ip
	 * 
	 * @return
	 * @since DSS 1.0
	 */
	public static List<String> getCheifReceiver() {
		return null;
	}

	/**
	 * 根据角色获取对应的ip
	 * 
	 * @param roleType
	 * @return
	 * @throws IOException
	 * @since DSS 1.0
	 */
	@SuppressWarnings("rawtypes")
	public static String getCommandReceiver(RoleType roleType)
			throws IOException {
		if (roleType == null) {
			return null;
		} else {
			Properties pros = new Properties();
			InputStream in = NetPropertiesUtil.class
					.getResourceAsStream(IP_ADDRESS_FILE);
			pros.load(in);
			in.close();

			String ip = null;
			for (Entry entry : pros.entrySet()) {
				if (roleType.getKeyWord().equals(entry.getKey())) {
					ip = (String) entry.getValue();
					break;
				}
			}
			return ip;
		}
	}

	/**
	 * 删除连接不上或未知的IP信息
	 * 
	 * @throws IOException
	 * @since DSS 1.0
	 */
	@SuppressWarnings("rawtypes")
	public static void removeIPAddress(String ip) throws IOException {
		Properties pros = new Properties();
		InputStream in = NetPropertiesUtil.class
				.getResourceAsStream(IP_ADDRESS_FILE);
		pros.load(in);
		in.close();

		for (Entry entry : pros.entrySet()) {
			if (ip.equals(entry.getValue())) {
				pros.remove(entry);
			}
		}

		OutputStream out = new FileOutputStream(IP_ADDRESS_FILE);
		pros.store(out, "address");
		out.close();
	}

	/**
	 * 清空所有ip
	 * 
	 * @throws IOException
	 * 
	 * @since DSS 1.0
	 */
	public static void clearAll() throws IOException {
		Properties pros = new Properties();
		InputStream in = NetPropertiesUtil.class
				.getResourceAsStream(IP_ADDRESS_FILE);
		pros.load(in);
		in.close();

		pros.clear();
	}

}
