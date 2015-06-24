package nuist.qlib.dss.net;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.FutureTask;

import nuist.qlib.dss.dao.AddressManager;

/**
 * 类名：MainClientOutputThread 功能：管理客户端信息发送
 */
public class MainClientOutputThread {

	private AddressManager adressManager;

	/*
	 * 构造函数，初始化ip地址接口
	 */
	public MainClientOutputThread() {
		this.adressManager = new AddressManager();
	}

	/**
	 * 发送队伍信息
	 * 
	 * @param teamReceiver
	 * @param team
	 * @param category
	 * @param matchName
	 * @return
	 */
	public int sendTeam(String teamReceiver[], String team, String category,
			String matchName) {
		try {
			List<String> list = new ArrayList<String>();
			list = adressManager.getIP(teamReceiver); // 获取IP
			List<FutureTask<Integer>> tasks = new ArrayList<FutureTask<Integer>>();
			int sum = 1;

			// 遍历获取的IP，并发送
			if (list.size() == 0) {// 未收到ip
				sum = 0;
			} else {
				for (int i = 0; i < list.size(); i++) {
					FutureTask<Integer> task = new FutureTask<Integer>(
							new ClientOutputThread(list.get(i), team, category,
									matchName));
					tasks.add(task);
					new Thread(task).start();
				}

				for (FutureTask<Integer> task : tasks)
					// 获取线程返回值
					sum *= task.get();
			}
			return sum;
		} catch (Exception e1) {
			return -1;// 发送失败
		}
	}

	/**
	 * @author liuchao
	 * @version 2014-4-22 上午9:39:42
	 * @Description: TODO
	 * @param scoreReceiver
	 * @param artScore1
	 * @param artScore2
	 * @param artScore3
	 * @param artScore4
	 * @param artTotalScore
	 * @param completionScore1
	 * @param completionScore2
	 * @param completionScore3
	 * @param completionScore4
	 * @param completionTotalScore
	 * @param difficultScore
	 * @param difficultSubScore
	 * @param deduction_score
	 * @param total
	 * @return
	 * @throws
	 */
	public int sendScore(String scoreReceiver[], String artScore1,
			String artScore2, String artScore3, String artScore4,
			String artTotalScore, String completionScore1,
			String completionScore2, String completionScore3,
			String completionScore4, String completionTotalScore,
			String difficultScore, String difficultSubScore,
			String deduction_score, String total) {
		try {
			List<String> list = new ArrayList<String>();
			list = adressManager.getIP(scoreReceiver); // 获取IP
			List<FutureTask<Integer>> tasks = new ArrayList<FutureTask<Integer>>();
			int sum = 1;

			// 遍历获取的IP，并发送
			if (list.size() == 0) {// 未收到ip
				sum = 0;
			} else {
				for (int i = 0; i < list.size(); i++) {
					FutureTask<Integer> task = new FutureTask<Integer>(
							new ClientOutputThread(list.get(i), artScore1,
									artScore2, artScore3, artScore4,
									artTotalScore, completionScore1,
									completionScore2, completionScore3,
									completionScore4, completionTotalScore,
									difficultScore, difficultSubScore,
									deduction_score, total));
					tasks.add(task);
					new Thread(task).start();
				}

				for (FutureTask<Integer> task : tasks)
					// 获取线程返回值
					sum *= task.get();
			}
			return sum;
		} catch (Exception e1) {
			return -1;// 发送失败
		}
	}

	/**
	 * @Title: sendCommand
	 * @Description: TODO(给打分裁判发送指令)
	 * @param commmandReceiver
	 * @param command
	 * @return
	 */
	public int sendCommand(String commmandReceiver[], String command) {
		try {
			List<String> list = new ArrayList<String>();
			list = adressManager.getIP(commmandReceiver); // 获取IP
			List<FutureTask<Integer>> tasks = new ArrayList<FutureTask<Integer>>();
			int sum = 1;

			// 遍历获取的IP，并发送
			if (list.size() == 0) {// 未收到ip
				sum = 0;
			} else {
				for (int i = 0; i < list.size(); i++) {
					FutureTask<Integer> task = new FutureTask<Integer>(
							new ClientOutputThread(list.get(i), command));
					tasks.add(task);
					new Thread(task).start();
				}

				for (FutureTask<Integer> task : tasks)
					// 获取线程返回值
					sum *= task.get();
			}
			return sum;
		} catch (Exception e1) {
			return -1;// 发送失败
		}
	}
}
