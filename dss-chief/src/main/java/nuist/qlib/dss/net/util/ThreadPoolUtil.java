package nuist.qlib.dss.net.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class ThreadPoolUtil {

	private static final ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(1);
	
	private static final ExecutorService executor = Executors.newFixedThreadPool(5);
	
	/**
	 * 获取周期运行线程执行器
	 * 
	 * @return
	 * @since DSS 1.0
	 */
	public static ScheduledExecutorService getScheduledExecutorServiceInstance() {
		return scheduledExecutor;
	}
	
	/**
	 * 获取普通线程执行器
	 * 
	 * @return
	 * @since DSS 1.0
	 */
	public static ExecutorService getExecutorServiceInstance() {
		return executor;
	}
	
}
