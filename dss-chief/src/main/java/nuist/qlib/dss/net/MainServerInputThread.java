package nuist.qlib.dss.net;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.log4j.Logger;

/**
 * 
 * 接收主线程
 * 
 */
public class MainServerInputThread extends Thread {

	private ServerSocket serverSocket;
	private Logger logger;

	public MainServerInputThread(ServerSocket serverSocket) {
		logger = Logger.getLogger(MainServerInputThread.class.getName());
		this.serverSocket = serverSocket;
	}

	public void run() {
		try {
			while (true) {
				Socket socket = serverSocket.accept(); // 监听
				new ServerInputThread(socket).start(); // 监听到数据之后启用线程
			}
		} catch (IOException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} // 监听端口号
			// 调用socket的accept()方法侦听并接受到此套接字的连接，此方法在连接传入之前一直阻塞。
	}
}