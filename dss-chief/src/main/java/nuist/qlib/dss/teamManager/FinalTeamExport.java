package nuist.qlib.dss.teamManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import nuist.qlib.dss.dao.FinalOperDao;

public class FinalTeamExport {
	private FinalOperDao dao;

	public FinalTeamExport() {
		dao = new FinalOperDao();
	}

	/***
	 * 获得预赛的该场比赛的所有项目名称
	 * 
	 * @param matchName
	 * @return 返回的List中键值对为category,count(该项目中总共有多少个队伍)
	 */
	public List<HashMap<String, Object>> getAllCategory(String matchName) {
		List<HashMap<String, Object>> data = dao.getAllCategory(matchName);
		int count;
		for (HashMap<String, Object> one : data) {
			count = one.get("hasMatchCount") == null ? 0 : Integer.valueOf(one
					.get("hasMatchCount").toString());
			if (count > 8) {
				one.put("exportCount", 8);
			} else {
				one.put("exportCount", count);
			}
		}
		return data;
	}

	/***
	 * 一键式导出决赛队伍
	 * 
	 * @param matchName
	 *            赛事名称
	 * @param category
	 *            其中包括键值category,count(该项目中要导出前多少个队伍)
	 * @return 返回的List中键值对为match_order表中的id
	 */
	public List<HashMap<String, Object>> getFinalTeamAll(
			List<HashMap<String, Object>> categorys, String matchName) {
		List<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
		List<HashMap<String, Object>> result = new ArrayList<HashMap<String, Object>>();

		for (HashMap temp : categorys) {
			result = dao.getTeamByCategory(
					Integer.valueOf(temp.get("selectCount").toString()),
					matchName, temp.get("category").toString());

			for (int i = result.size() - 1; i >= 0; i--) {
				data.add(result.get(i));
			}
		}
		return data;
	}

	/***
	 * 判断是否成功的连接了数据库
	 * 
	 * @return
	 */
	public boolean isCollected() {
		return dao.isCollected();
	}

	public void close() {
		dao.close();
	}
}
