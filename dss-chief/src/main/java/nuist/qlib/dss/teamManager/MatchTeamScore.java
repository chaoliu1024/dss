/*
 * 文件名：MatchTeamScore.java
 * 版权：Copyright 2014 Artisan WangFang
 * 描述：比赛时,所有队伍信息及队伍成绩操作
 */

package nuist.qlib.dss.teamManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import nuist.qlib.dss.dao.TeamScoreDao;

/**
 * @author liuchao
 *
 */
public class MatchTeamScore {

	TeamScoreDao dao;

	public MatchTeamScore() {
		dao = new TeamScoreDao();
	}

	/**
	 * @author liuchao
	 * @version 2014-3-28 下午6:19:24
	 * @Description: 得到未进行比赛的场次
	 * @return List<Integer> 场次List
	 * @throws
	 */
	public List<String> getMatchNum(String matchName) {
		List<HashMap<String, Object>> data = dao.getMatchNum(matchName);
		List<String> results = new ArrayList<String>();
		for (int i = 0; i < data.size(); i++) {
			results.add(data.get(i).get("matchNum").toString());
		}
		return results;
	}

	/**
	 * @author liuchao
	 * @version 2014-3-31 上午10:35:47
	 * @Description 获得初始时的比赛场次
	 * @param matchName
	 *            赛事名称
	 * @return String
	 * @throws
	 */
	public int getInitMatchNum(String matchName) {
		List<HashMap<String, Object>> data = dao.getInitMatchNum(matchName);
		int result = Integer.parseInt(data.get(0).get("matchNum").toString());
		return result;
	}

	/**
	 * @author liuchao
	 * @version 2014-3-31 上午11:16:13
	 * @Description 获取赛事模式
	 * @param matchName
	 *            赛事名称
	 * @param matchNum
	 *            比赛场次
	 * @return List<String>
	 * @throws
	 */
	public List<String> getMatchType(String matchName, String matchNum) {
		List<HashMap<String, Object>> data = dao.getMatchType(matchName,
				matchNum);
		List<String> results = new ArrayList<String>();
		for (int i = 0; i < data.size(); i++) {
			if (data.get(i).get("finalPreliminary").toString().equals("false"))
				results.add("预赛");
			if (data.get(i).get("finalPreliminary").toString().equals("true"))
				results.add("决赛");
		}
		return results;
	}

	/**
	 * 
	 * @author liuchao
	 * @version 2014-3-31 下午2:30:05
	 * @Description: 得到初始第一个参赛队伍信息
	 * @param matchNum
	 *            比赛场次
	 * @return HashMap<String,Object>
	 * @throws
	 */
	public List<HashMap<String, Object>> getInitTeam(int matchNum,
			String matchName) {
		List<HashMap<String, Object>> data = dao.getInitTeam(matchNum,
				matchName);
		return data;
	}

	/**
	 * @author liuchao
	 * @version 2014-3-31 上午10:25:55
	 * @Description 得到赛事名称
	 * @return String
	 * @throws
	 */
	public String[] getMatchName() {
		List<HashMap<String, Object>> data = dao.getMatchName();
		if (data.size() > 0) {
			String[] result = new String[data.size()];
			for (int i = 0; i < data.size(); i++) {
				result[i] = data.get(i).get("matchName").toString();
			}
			return result;
		} else {
			return null;
		}
	}

	/**
	 * @author liuchao
	 * @version 2014-3-31 下午5:14:22
	 * @Description: 更改队伍状态
	 * @param matchOrder
	 *            出场顺序
	 * @param status
	 *            队伍状态信息:'1'已比赛, '2'延迟比赛, '3'弃权
	 * @return '1'更新成功，'-1'更新失败
	 * @throws
	 */
	public int updateTeamStatu(int id, int status) {
		return dao.updateTeamStatu(id, status);
	}

	/**
	 * @Description insert score according to id
	 * @author LiuChao
	 * @date Sep 6, 2014 2:15:07 PM
	 */
	public int insertScore(int id, String artScore01, String artScore02,
			String artScore03, String artScore04, String artTotalScore,
			String execScore01, String execScore02, String execScore03,
			String execScore04, String execTotalScore, String impScore01,
			String impScore02, String impTotalScore, String subScore,
			String totalScore, double score_error1, double score_error2,
			double score_error3, double score_error4, double score_error5,
			double score_error6, double score_error7, double score_error8,
			double score_error9, double score_error10) {

		Boolean isInserted = dao.selectOneTeam(id);
		if (isInserted) {
			// 更新成绩
			return dao.updateTeamScore(id, artScore01, artScore02, artScore03,
					artScore04, artTotalScore, execScore01, execScore02,
					execScore03, execScore04, execTotalScore, impScore01,
					impScore02, impTotalScore, subScore, totalScore,
					score_error1, score_error2, score_error3, score_error4,
					score_error5, score_error6, score_error7, score_error8,
					score_error9, score_error10);
		} else {
			// 插入成绩
			return dao.insertTeamScore(id, artScore01, artScore02, artScore03,
					artScore04, artTotalScore, execScore01, execScore02,
					execScore03, execScore04, execTotalScore, impScore01,
					impScore02, impTotalScore, subScore, totalScore,
					score_error1, score_error2, score_error3, score_error4,
					score_error5, score_error6, score_error7, score_error8,
					score_error9, score_error10);
		}
	}

	/**
	 * 
	 * @author liuchao
	 * @version 2014-3-31 下午11:43:26
	 * @Description 得到下一只队伍的相关信息
	 * @param matchType
	 *            赛事模式：预赛/决赛
	 * @param matchOrder
	 *            出场顺序
	 * @param match_num
	 *            组别
	 * @return
	 * @throws
	 */
	public List<HashMap<String, Object>> getNextTeam(int matchOrder,
			int match_num, String matchName) {
		List<HashMap<String, Object>> data = dao.getNextTeam(matchOrder,
				match_num, matchName);
		return data;
	}

	/**
	 * @author liuchao
	 * @version 2014-4-1 上午7:22:35
	 * @Description 返回暂停比赛的队伍
	 * @param matchName
	 *            赛事名称
	 * @param matchType
	 *            赛事模式：预赛/决赛
	 * @return
	 * @throws
	 */
	public List<String> getNoRaceTeam(String matchName, int matchType) {
		List<HashMap<String, Object>> data = dao.getNoRaceTeam(matchName,
				matchType);
		List<String> teamName = new ArrayList<String>();
		for (int i = 0; i < data.size(); i++) {
			teamName.add(data.get(i).get("teamName").toString());
		}
		return teamName;
	}

	/**
	 * @author liuchao
	 * @version 2014-4-1 上午7:34:30
	 * @Description 返回暂停比赛队伍的项目
	 * @param matchName
	 *            赛事名称
	 * @param match_num
	 *            比赛场次
	 * @param team
	 *            参赛队伍
	 * @param matchType
	 *            赛事模式：预赛/决赛
	 * @return
	 * @throws
	 */
	public List<String> getNoRaceCategory(String matchName, String team,
			int matchType) {
		List<HashMap<String, Object>> data = dao.getNoRaceCategory(matchName,
				team, matchType);
		List<String> category = new ArrayList<String>();
		for (int i = 0; i < data.size(); i++) {
			category.add(data.get(i).get("category").toString());
		}
		return category;
	}

	/**
	 * @author liuchao
	 * @version 2014-4-1 上午7:42:42
	 * @Description 得到暂停比赛的队伍ID
	 * @param matchName
	 *            赛事名称
	 * @param match_num
	 *            比赛场次
	 * @param matchType
	 *            赛事模式：预赛/决赛
	 * @param team
	 *            参赛队伍
	 * @param category
	 *            参赛项目
	 * @return
	 * @throws
	 */
	public int getNoRaceTeamID(String matchName, int matchType, String team,
			String category) {
		List<HashMap<String, Object>> data = dao.getNoRaceTeamID(matchName,
				matchType, team, category);
		int re_id = Integer.parseInt(String.valueOf(data.get(0).get("re_id")));
		return re_id;
	}

	/**
	 * @author liuchao
	 * @version 2014-4-3 下午12:12:47
	 * @Description: 得到所有需要进行补赛的赛事名称
	 * @return
	 * @throws
	 */
	public String[] getAllReplayMatchNames() {
		List<HashMap<String, Object>> data = dao.getAllReplayMatchNames();
		String[] results = new String[data.size()];
		for (int i = 0; i < data.size(); i++) {
			results[i] = data.get(i).get("matchName").toString();
		}
		return results;
	}

	public boolean isCollected() {
		return dao.isCollected();
	}

	public void close() {
		dao.close();
	}
}
