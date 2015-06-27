/*
 * Copyright (c) 2015, NUIST - 120Lib. All rights reserved.
 */

package nuist.qlib.dss.dao;

import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

public class HandlerDataDao {
	// 连接数据库需要的变量
	private ConnSQL connSql;;
	private Logger logger;

	/** 构造函数，读取数据库的配置，同时与数据库建立连接 */
	public HandlerDataDao() {
		logger = Logger.getLogger(HandlerDataDao.class.getName());
		connSql = new ConnSQL();
	}

	/** 判断是否连接到了数据库 */
	public boolean isCollected() {
		return connSql.isConnected();
	}

	/***
	 * 关闭连接
	 */
	public void close() {
		connSql.close();
	}

	/** 删除历史数据 */
	public boolean deleteAllData(String matchName) {
		String lalaScoreSql = "delete from score where team_id in(select id from match_order where match_name=?)"; // 删除团体成绩
		String lalaInfoSql = "delete from match_order where match_name=?"; // 删除团体信息

		try {
			// 删除团体成绩
			if (connSql.deleteObject(lalaScoreSql, new Object[] { matchName })) {
				if (connSql.deleteObject(lalaInfoSql,
						new Object[] { matchName })) {
					return true;
				} else
					return false;
			} else
				return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/***
	 * 返回所有有成绩的赛事
	 * 
	 * @return
	 */
	public List<HashMap<String, Object>> getAllScoreMatchName() {
		String hasScoreMatcheSql = "select distinct a.match_name as matchName from match_order as a,score as b where a.id=b.team_id";
		return connSql.selectQuery(hasScoreMatcheSql, new Object[0]);
	}

	/**
	 * 返回所有赛事
	 * 
	 * @return
	 */
	public List<HashMap<String, Object>> getAllMatchName() {
		String matchesSql = "select distinct match_name as matchName from match_order";
		return connSql.selectQuery(matchesSql, new Object[0]);
	}

	/***
	 * 删除赛事
	 * 
	 * @param matches
	 *            格式："","",""
	 * @return
	 */
	public boolean deleteMatches(String matches) {
		if (connSql
				.deleteObject(
						"delete from score where team_id in (select id from match_order where match_name in ("
								+ matches + "))", new Object[0])) {
			if (connSql.deleteObject(
					"delete from match_order where match_name in(" + matches
							+ ")", new Object[0])) {
				return true;
			} else
				return false;
		} else
			return false;
	}
}
