package nuist.qlib.dss.thread;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

import nuist.qlib.dss.ui.RankPanel;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class SplashThread implements Runnable {
	private boolean flag;
	private boolean isStop;
	private String ip;
	private String port;
	private String baseName;
	private String userName;
	private String userPwd;
	private Connection conn;
	private Shell shell;

	public SplashThread(Shell shell) {
		isStop = false;
		flag = false;
		this.shell = shell;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while (!isStop) {
			readParams();
			if (conn == null) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				isStop = true;
				Display.getDefault().syncExec(new Runnable() {
					public void run() {
						shell.dispose();
						RankPanel window = new RankPanel();
						window.open();
					}
				});
			}
		}
	}

	public void readParams() {
		Properties props = new Properties();
		try {
			InputStream in = SplashThread.class
					.getResourceAsStream("/dataBase.properties");
			props.load(in);
			port = props.getProperty("port");
			ip = props.getProperty("ip");
			baseName = props.getProperty("baseName");
			userName = props.getProperty("userName");
			userPwd = props.getProperty("userPwd");
			conn = connectDataBase();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/** 连接数据库 */
	public Connection connectDataBase() {
		Connection conn = null;
		if (ip == null || ip.equals("") || ip.equals("null") || port == null
				|| port.equals("") || baseName == null || baseName.equals("")
				|| userName == null || userName.equals("") || userPwd == null
				|| userPwd.equals("")) {
			return conn;
		} else {
			try {
				String url = "jdbc:sqlserver://" + ip + ":" + port
						+ ";DatabaseName=" + baseName;
				conn = DriverManager.getConnection(url, userName, userPwd);
				return conn;
			} catch (Exception e) {
				// e.printStackTrace();
				return conn;
			}
		}
	}

	public boolean isFlag() {
		return flag;
	}

	public void setFlag(boolean flag) {
		this.flag = flag;
	}
}
