/*
 * 文件名：QueryScoreDao.java
 * 版权：Copyright 2014 Artisan WangFang
 * 描述：成绩查询Dao，包括查询已有成绩的赛事名称
 */

package nuist.qlib.dss.dao;

import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

public class QueryScoreDao {
	// 连接数据库需要的变量
	private ConnSQL connSql = new ConnSQL();
	private Logger logger;

	/** 构造函数，读取数据库的配置，同时与数据库建立连接 */
	public QueryScoreDao() {
		connSql = new ConnSQL();
	}

	/** 判断是否连接到了数据库 */
	public boolean isCollected() {
		return connSql.isConnected();
	}

	/** 关闭数据库链接 **/
	public void close() {
		connSql.close();
	}

	/***
	 * 获取所有已有成绩的赛事名称
	 * 
	 * @return
	 */
	public List<HashMap<String, Object>> getAllMatchNames() {
		String sql = "select distinct a.match_name as matchName from match_order a,score as b where a.id=b.team_id";
		List<HashMap<String, Object>> data = connSql.selectQuery(sql,
				new Object[0]);
		return data;
	}

	/**
	 * 在首界面点击排名按钮，若此时界面中没有任何参赛队伍的order，那么根据赛事名称以及赛事类型选择id号最小的队伍
	 * 
	 * @param matchType
	 *            赛事类型
	 * @param matchName
	 *            赛事名称
	 * @return
	 */
	public List<HashMap<String, Object>> getTeamMinId(int matchType,
			String matchName) {
		String sql = "select min(a.team_id) as id from score as a,match_order as b where a.team_id=b.id and b.final_preliminary=? and b.match_name=?";
		List<HashMap<String, Object>> data = connSql.selectQuery(sql,
				new Object[] { matchType, matchName });
		return data;
	}

	/**
	 * 在首界面点击排名按钮，若此时界面中有任何参赛队伍的order，那么根据下列条件选择order对应的队伍
	 * 
	 * @param matchType
	 *            赛事类型
	 * @param matchName
	 *            赛事名称
	 * @param matchNum
	 *            场次
	 * @param matchOrder
	 *            队伍在场次中的序号
	 * @return
	 */
	public List<HashMap<String, Object>> getTeamMinId(int matchType,
			String matchName, int matchNum, int matchOrder) {
		String sql = "select b.id as id from match_order as b where b.final_preliminary=? and b.match_name=? and b.match_num=? and b.match_order=?";
		List<HashMap<String, Object>> data = connSql.selectQuery(sql,
				new Object[] { matchType, matchName, matchNum, matchOrder });
		return data;
	}

	/** 参数为参赛者的id号,该函数根据参赛者的id号、比赛类型(如预赛或决赛)显示参赛者所属项目的排名情况 ,按照总分排名 */
	public List<HashMap<String, Object>> getRank(int id, int matchType) {
		String sql = null;
		sql = "select b.id as id,b.match_units as teamName,b.match_num as matchNum,b.member_name as memberName,a.score01_art,a.score01_execution,a.score01_impression as score01_impression,a.score02_impression as score02_impression,a.avg_impression as avg_impression,a.score02_art,a.score02_execution,a.score03_art,a.score03_execution,a.score04_art,a.score04_execution,a.avg_art,a.avg_execution,a.sub_score as sub_score,a.total as total,a.id as scoreId,b.match_category as category from score as a,match_order as b where a.team_id=b.id and b.match_category=(select match_category from match_order where id=?) and b.match_name=(select match_name from match_order where id=?) and b.final_preliminary=? "
				+ "order by a.total desc";
		List<HashMap<String, Object>> data = connSql.selectQuery(sql,
				new Object[] { id, id, matchType });
		return data;
	}

	/** 根据站点、比赛项目、比赛类型获取该项目的比赛排名成绩 */
	public List<HashMap<String, Object>> getRank(String matchName,
			String category, int matchType) {
		String sql = "select b.id as id,b.match_units as teamName,b.match_num as matchNum,a.score01_art,a.score01_execution,a.score01_impression as score01_impression,a.score02_impression as score02_impression,a.avg_impression as avg_impression,a.score02_art,a.score02_execution,a.score03_art,a.score03_execution,a.score04_art,a.score04_execution,a.avg_art,a.avg_execution,a.sub_score as sub_score,a.total as total,a.id as scoreId,b.match_category as category from score as a,match_order as b where a.team_id=b.id and b.match_category=? and b.match_name=? and b.final_preliminary=? "
				+ "order by a.total desc";
		List<HashMap<String, Object>> data = connSql.selectQuery(sql,
				new Object[] { category, matchName, matchType });
		return data;
	}

	/** 根据站点、比赛项目、比赛类型获取该项目的比赛排名成绩 */
	public List<HashMap<String, Object>> getPrintRank(String matchName,
			String category, int matchType) {
		String sql = "select b.id as id,b.match_units as teamName,b.member_name as memberName,b.match_num as matchNum,b.member_name as memberName,a.score01_art,a.score01_execution,a.score01_impression as score01_impression,a.score02_impression as score02_impression,a.avg_impression as avg_impression,a.score02_art,a.score02_execution,a.score03_art,a.score03_execution,a.score04_art,a.score04_execution,a.avg_art,a.avg_execution,a.sub_score as sub_score,a.total as total,a.id as scoreId,b.match_category as category from score as a,match_order as b where a.team_id=b.id and b.match_category=? and b.match_name=? and b.final_preliminary=? "
				+ "order by a.total desc";
		List<HashMap<String, Object>> data = connSql.selectQuery(sql,
				new Object[] { category, matchName, matchType });
		return data;
	}

	/**
	 * 根据id号返回它所属的项目
	 * 
	 * @param id
	 * @return
	 */
	public String getCategory(int id) {
		String sql = "select match_category as category from match_order where id=?";
		List<HashMap<String, Object>> data = connSql.selectQuery(sql,
				new Object[] { id });
		return data.get(0).get("category").toString();
	}

	/**
	 * 获得比赛的项目类型
	 * 
	 * @param matchType
	 *            预赛或决赛
	 */
	public List<HashMap<String, Object>> getCategories(int matchType,
			String matchName) {
		String sql = "select distinct a.match_category as category from match_order a,score b where a.id=b.team_id and a.final_preliminary=? and a.match_name=?";
		List<HashMap<String, Object>> data = connSql.selectQuery(sql,
				new Object[] { matchType, matchName });
		return data;
	}

	/***
	 * 根据已有成绩赛事名称返回该下面的赛事模式
	 * 
	 * @param matchName
	 * @return
	 */
	public List<HashMap<String, Object>> getMatchKindByMatchName(
			String matchName) {
		String sql = "select distinct a.final_preliminary as matchKind from match_order a,score b where a.id=b.team_id and a.match_name=?";
		List<HashMap<String, Object>> data = connSql.selectQuery(sql,
				new Object[] { matchName });
		return data;
	}

	/***
	 * 根据赛事名称获取该赛事中的裁判的误差值
	 * 
	 * @param matchName
	 * @return
	 */
	public List<HashMap<String, Object>> getDeviations(String matchName,
			int matchType) {
		String sql = "select avg(b.score01_art_error) as arterror1, avg(b.score02_art_error) as arterror2, avg(b.score03_art_error) as arterror3,avg(b.score04_art_error) as arterror4,avg(b.score01_execution_error) as execerror1, avg(b.score02_execution_error) as execerror2,avg(b.score03_execution_error) as execerror3, avg(b.score04_execution_error) as execerror4,avg(b.score01_impression_error) as imperror1,avg(b.score02_impression_error) as imperror2 from match_order a,score b where a.id=b.team_id and a.match_name=? and a.final_preliminary=?";
		List<HashMap<String, Object>> data = connSql.selectQuery(sql,
				new Object[] { matchName, matchType });
		return data;
	}
}
