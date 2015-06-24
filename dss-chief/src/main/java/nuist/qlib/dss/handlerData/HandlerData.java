/*
 * 文件名：HandlerData.java
 * 版权：Copyright 2014 Artisan WangFang
 * 描述：数据处理（包括下载和历史数据的处理）
 */
package nuist.qlib.dss.handlerData;

import java.util.HashMap;
import java.util.List;

import nuist.qlib.dss.dao.HandlerDataDao;

/***
 * 出场顺序排序
 * 
 * @author fanfan
 * 
 */
public class HandlerData {
	private HandlerDataDao dao;

	public HandlerData() {
		dao = new HandlerDataDao();
	}

	public boolean isCollected() {
		return dao.isCollected();
	}

	public void close() {
		if (dao.isCollected()) {
			dao.close();
		}
	}

	/***
	 * 返回赛事名称
	 * 
	 * @return result[0]字符串数组存放的是已经有成绩的赛事名称；result[1]字符串数组存放的是没有成绩的赛事名称
	 */
	public String[][] getMatchNames() {
		List<HashMap<String, Object>> hasScoreMatches = dao
				.getAllScoreMatchName();
		List<HashMap<String, Object>> matches = dao.getAllMatchName();
		String[][] results = new String[2][matches.size()];
		for (int i = 0; i < hasScoreMatches.size(); i++) {
			results[0][i] = hasScoreMatches.get(i).get("matchName").toString();
		}
		String matchName;
		int count = 0;
		for (int j = 0; j < matches.size(); j++) {
			matchName = matches.get(j).get("matchName").toString();
			int i = 0;
			for (; i < hasScoreMatches.size(); i++) {
				if (matchName.equals(hasScoreMatches.get(i).get("matchName")
						.toString())) {
					break;
				}
			}
			if (i == hasScoreMatches.size()) {
				results[1][count] = matchName;
				count++;
			}
		}
		return results;
	}

	/***
	 * 删除赛事
	 * 
	 * @param hasScoreMatches
	 *            要删除的有成绩的赛事
	 * @param hasNoScoreMatches
	 *            要删除的没有成绩的赛事
	 * @return 0表示删除成功，1表示删除有成绩的赛事失败；2表示删除没有成绩的赛事成功
	 */
	public int deleteMatches(List<String> hasScoreMatches,
			List<String> hasNoScoreMatches) {
		String hasScoreMatchesS = "";
		String hasNoScoreMatchesS = "";
		if (hasScoreMatches.size() != 0) {
			for (String temp : hasScoreMatches) {
				hasScoreMatchesS += "'" + temp + "',";
			}
			hasScoreMatchesS = hasScoreMatchesS.substring(0,
					hasScoreMatchesS.length() - 1);
		}
		if (hasNoScoreMatches.size() != 0) {
			for (String temp : hasNoScoreMatches) {
				hasNoScoreMatchesS += "'" + temp + "',";
			}
			hasNoScoreMatchesS = hasNoScoreMatchesS.substring(0,
					hasNoScoreMatchesS.length() - 1);
		}
		if (hasScoreMatchesS.trim().length() != 0) { // 删除有成绩的赛事
			if (!dao.deleteMatches(hasScoreMatchesS)) {
				return 1;
			}
		}
		if (hasNoScoreMatchesS.trim().length() != 0) { // 删除没有成绩的赛事
			if (!dao.deleteMatches(hasNoScoreMatchesS)) {
				return 2;
			}
		}
		return 0;
	}
}
