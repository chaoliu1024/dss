package nuist.qlib.dss.net;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

import nuist.qlib.dss.net.util.ThreadPoolUtil;

import org.apache.log4j.Logger;

/**
 * 
 * 接收主线程
 * 
 */
public class MainServerInputThread implements Runnable {

	private Logger logger;

	public MainServerInputThread() {
		logger = Logger.getLogger(MainServerInputThread.class.getName());
	}

	public void run() {
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(6666);
			ExecutorService executor = ThreadPoolUtil.getExecutorServiceInstance();
			while (true) {
				Socket socket = serverSocket.accept(); // 监听
				executor.submit(new ServerInputThread(socket));// 监听到数据之后启用线程
			}
		} catch (IOException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} // 监听端口号
			// 调用socket的accept()方法侦听并接受到此套接字的连接，此方法在连接传入之前一直阻塞。
	}
}