package nuist.qlib.dss.net;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.FutureTask;

import nuist.qlib.dss.net.util.NetPropertiesUtil;
import nuist.qlib.dss.net.util.ThreadPoolUtil;
import nuist.qlib.dss.net.vo.AllScoreMessageVO;
import nuist.qlib.dss.net.vo.CommandMessageVO;
import nuist.qlib.dss.net.vo.MatchInfoMessageVO;

import org.apache.commons.lang.StringUtils;

/**
 * 类名：MainClientOutputThread 功能：管理客户端信息发送
 */
public class MainClientOutputThread {

	/**
	 * 发送队伍信息
	 * 
	 * @param matchInfoMessageVO
	 * @return
	 * @since DSS 1.0
	 */
	public int sendTeam(MatchInfoMessageVO matchInfoMessageVO) {
		try {
			List<String> list = new ArrayList<String>();
			list = NetPropertiesUtil.getTeamReceiver();
			List<FutureTask<Integer>> tasks = new ArrayList<FutureTask<Integer>>();
			int sum = 1;

			// 遍历获取的IP，并发送
			if (list.size() == 0) {// 未收到ip
				sum = 0;
			} else {
				ExecutorService executor = ThreadPoolUtil
						.getExecutorServiceInstance();
				for (int i = 0; i < list.size(); i++) {
					matchInfoMessageVO.setTargetIp(list.get(i));
					tasks.add((FutureTask<Integer>) executor
							.submit(new ClientOutputThread(matchInfoMessageVO)));
				}

				for (FutureTask<Integer> task : tasks)
					// 获取线程返回值
					sum *= task.get();
			}
			return sum;
		} catch (Exception e) {
			return -1;// 发送失败
		}
	}

	/**
	 * 发送所有得分给高级裁判组
	 * 
	 * @param allScoreMessageVO
	 * @return
	 * @since DSS 1.0
	 */
	public int sendScore(AllScoreMessageVO allScoreMessageVO) {
		try {
			List<String> list = new ArrayList<String>();
			list = NetPropertiesUtil.getCheifReceiver(); // 获取IP
			List<FutureTask<Integer>> tasks = new ArrayList<FutureTask<Integer>>();
			int sum = 1;

			// 遍历获取的IP，并发送
			if (list.size() == 0) {// 未收到ip
				sum = 0;
			} else {
				ExecutorService executor = ThreadPoolUtil
						.getExecutorServiceInstance();
				for (int i = 0; i < list.size(); i++) {
					allScoreMessageVO.setTargetIp(list.get(i));
					tasks.add((FutureTask<Integer>) executor
							.submit(new ClientOutputThread(allScoreMessageVO)));
				}

				for (FutureTask<Integer> task : tasks)
					// 获取线程返回值
					sum *= task.get();
			}
			return sum;
		} catch (Exception e) {
			return -1;// 发送失败
		}
	}

	/**
	 * 给指定裁判发送调分指令
	 * 
	 * @param commandMessageVO
	 * @return
	 * @since DSS 1.0
	 */
	public int sendCommand(CommandMessageVO commandMessageVO) {
		try {
			// 获取IP
			String ip;
			ip = NetPropertiesUtil.getCommandReceiver(commandMessageVO
					.getRoleType());
			if (StringUtils.isBlank(ip)) {
				return 0;
			}

			// 发送
			commandMessageVO.setTargetIp(ip);
			ExecutorService executor = ThreadPoolUtil
					.getExecutorServiceInstance();
			FutureTask<Integer> task = (FutureTask<Integer>) executor
					.submit(new ClientOutputThread(commandMessageVO));
			return task.get();
		} catch (IOException e) {
			return -1;
		} catch (InterruptedException e) {
			return -1;
		} catch (ExecutionException e) {
			return -1;
		}

	}
}
