/*
 * 文件名：TeamScoreDao.java
 * 版权：Copyright 2014 Artisan WangFang
 * 描述：比赛进行时队伍、成绩的数据库操作
 */

package nuist.qlib.dss.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * @author liuchao
 * 
 */
public class TeamScoreDao {

	// 连接数据库需要的变量
	private ConnSQL connSql;

	@SuppressWarnings("unused")
	private Logger logger;

	PreparedStatement st;
	Connection conn;

	/** 构造函数，读取数据库的配置，同时与数据库建立连接 */
	public TeamScoreDao() {
		logger = Logger.getLogger(TeamScoreDao.class.getName());
		this.connSql = new ConnSQL();
		conn = connSql.connectDataBase();
	}

	/** 判断是否连接到了数据库 */
	public boolean isCollected() {
		return connSql.isConnected();
	}

	/** 关闭数据库链接 **/
	public void close() {
		connSql.close();
	}

	/**
	 * 
	 * @author liuchao
	 * @version 2014-3-31 上午10:20:51
	 * @Description: 获取赛事名称
	 * @return List<HashMap<String,Object>>
	 * @throws
	 */
	public List<HashMap<String, Object>> getMatchName() {
		String sql = "select distinct match_name as matchName from match_order where unit_status = 0";
		List<HashMap<String, Object>> data = connSql.selectQuery(sql,
				new Object[0]);
		return data;
	}

	/**
	 * 
	 * @author liuchao
	 * @version 2014-3-28 下午6:48:33
	 * @Description: 获取未进行比赛的场次
	 * @return List<Integer> 未进行比赛的场次列表
	 * @throws
	 */
	public List<HashMap<String, Object>> getMatchNum(String matchName) {
		String sql = "select Distinct(match_num) as matchNum from match_order where unit_status = 0 and match_name = ?";
		List<HashMap<String, Object>> data = connSql.selectQuery(sql,
				new Object[] { matchName });
		return data;
	}

	/**
	 * 
	 * @author liuchao
	 * @version 2014-3-31 上午11:16:54
	 * @Description: 获取初始时的比赛场次
	 * @param matchName
	 * @return List<HashMap<String,Object>>
	 * @throws
	 */
	public List<HashMap<String, Object>> getInitMatchNum(String matchName) {
		String sql = "select min(match_num) as matchNum from match_order where unit_status = 0 and match_name = ?";
		List<HashMap<String, Object>> data = connSql.selectQuery(sql,
				new Object[] { matchName });
		return data;
	}

	/**
	 * @author liuchao
	 * @version 2014-3-31 下午4:58:37
	 * @Description: 获取比赛类型：预赛/决赛
	 * @param matchName
	 *            赛事名称
	 * @param matchNum
	 *            比赛场次
	 * @return List<HashMap<String,Object>>
	 * @throws
	 */
	public List<HashMap<String, Object>> getMatchType(String matchName,
			String matchNum) {
		String sql = "select Distinct(final_preliminary) as finalPreliminary from match_order where match_num = ? and match_name = ? and unit_status = 0";
		List<HashMap<String, Object>> data = connSql.selectQuery(sql,
				new Object[] { matchNum, matchName });
		return data;
	}

	/**
	 * @author liuchao
	 * @version 2014-3-31 下午1:59:37
	 * @Description: 获取初始队伍信息
	 * @param matchNum
	 * @param matchType
	 * @return List<HashMap<String,Object>>
	 * @throws
	 */
	public List<HashMap<String, Object>> getInitTeam(int matchNum,
			String matchName) {
		String sql = "select id as id, match_order as matchOrder, match_category as category, match_units as teamName, final_preliminary as final, match_name as matchName from match_order where unit_status=0 and match_num=? and match_name=? and match_order in(select MIN(match_order) from match_order where unit_status = 0 and match_num=? and match_name=?)";
		List<HashMap<String, Object>> data = connSql.selectQuery(sql,
				new Object[] { matchNum, matchName, matchNum, matchName });
		return data;
	}

	/**
	 * @author liuchao
	 * @version 2014-3-31 下午5:34:39
	 * @Description: 更新队伍状态
	 * @param matchOrder
	 *            出场顺序
	 * @param status
	 *            队伍状态
	 * @return
	 * @throws
	 */
	public int updateTeamStatu(int id, int status) {
		String sql = "update match_order set unit_status = ? where id = ?";
		return connSql.updateObject(sql, new Object[] { status, id });
	}

	/**
	 * 
	 * @author liuchao
	 * @version 2014-3-31 下午9:29:30
	 * @Description: 查询队伍记录
	 * @param id
	 *            : 队伍id
	 * @return
	 * @throws
	 */
	public Boolean selectOneTeam(int id) {
		String sql = "select team_id from score where team_id = ?";
		if ((connSql.selectQuery(sql, new Object[] { id })).size() > 0)
			return true;
		else
			return false;
	}

	/**
	 * @author LiuChao
	 * @date Sep 6, 2014 2:17:05 PM
	 */
	public int updateTeamScore(int id, String artScore01, String artScore02,
			String artScore03, String artScore04, String artTotalScore,
			String execScore01, String execScore02, String execScore03,
			String execScore04, String execTotalScore, String impScore01,
			String impScore02, String impTotalScore, String subScore,
			String totalScore, double score_error1, double score_error2,
			double score_error3, double score_error4, double score_error5,
			double score_error6, double score_error7, double score_error8,
			double score_error9, double score_error10) {
		String sql = "update score set score01_art = ?, score02_art = ?, score03_art = ?, score04_art = ?, avg_art = ?, score01_execution = ?, score02_execution = ?, score03_execution = ?, score04_execution = ?, avg_execution = ?, score01_impression = ?, score02_impression = ?, avg_impression = ?, sub_score = ?, total = ?, score01_art_error = ?, score02_art_error = ?, score03_art_error = ?, score04_art_error = ?, score01_execution_error = ?, score02_execution_error = ?, score03_execution_error = ?, score04_execution_error = ?, score01_impression_error = ?, score02_impression_error = ? where team_id = ?";
		return connSql.updateObject(sql, new Object[] { artScore01, artScore02,
				artScore03, artScore04, artTotalScore, execScore01,
				execScore02, execScore03, execScore04, execTotalScore,
				impScore01, impScore02, impTotalScore, subScore, totalScore,
				score_error1, score_error2, score_error3, score_error4,
				score_error5, score_error6, score_error7, score_error8,
				score_error9, score_error10, id });
	}

	/**
	 * 
	 * @author liuchao
	 * @version 2014-4-1 上午11:25:06
	 * @Description: 插入比赛成绩 10个误差分数
	 * @return
	 * @throws
	 */
	public int insertTeamScore(int id, String _artScore01, String _artScore02,
			String _artScore03, String _artScore04, String _artTotalScore,
			String _execScore01, String _execScore02, String _execScore03,
			String _execScore04, String _execTotalScore, String _impScore01,
			String _impScore02, String _impTotalScore, String _subScore,
			String _totalScore, double score_error1, double score_error2,
			double score_error3, double score_error4, double score_error5,
			double score_error6, double score_error7, double score_error8,
			double score_error9, double score_error10) {

		float artScore01 = Float.parseFloat(_artScore01);
		float artScore02 = Float.parseFloat(_artScore02);
		float artScore03 = Float.parseFloat(_artScore03);
		float artScore04 = Float.parseFloat(_artScore04);
		float artTotalScore = Float.parseFloat(_artTotalScore);
		float execScore01 = Float.parseFloat(_execScore01);
		float execScore02 = Float.parseFloat(_execScore02);
		float execScore03 = Float.parseFloat(_execScore03);
		float execScore04 = Float.parseFloat(_execScore04);
		float execTotalScore = Float.parseFloat(_execTotalScore);
		float impScore01 = Float.parseFloat(_impScore01);
		float impScore02 = Float.parseFloat(_impScore02);
		float impTotalScore = Float.parseFloat(_impTotalScore);
		float subScore = Float.parseFloat(_subScore);
		float totalScore = Float.parseFloat(_totalScore);

		String sql = "insert into score (team_id, score01_art, score02_art, score03_art, score04_art, avg_art, score01_execution, score02_execution, score03_execution, score04_execution, avg_execution, score01_impression, score02_impression, avg_impression, sub_score, total, score01_art_error, score02_art_error, score03_art_error, score04_art_error, score01_execution_error, score02_execution_error, score03_execution_error, score04_execution_error, score01_impression_error, score02_impression_error) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		return connSql.insertObject(sql, new Object[] { id, artScore01,
				artScore02, artScore03, artScore04, artTotalScore, execScore01,
				execScore02, execScore03, execScore04, execTotalScore,
				impScore01, impScore02, impTotalScore, subScore, totalScore,
				score_error1, score_error2, score_error3, score_error4,
				score_error5, score_error6, score_error7, score_error8,
				score_error9, score_error10 });
	}

	/**
	 * @author liuchao
	 * @version 2014-4-1 上午7:17:38
	 * @Description: 得到延迟比赛的队伍信息
	 * @param matchName
	 *            赛事名称
	 * @param matchType
	 *            赛事模式：预赛/决赛
	 * @return
	 * @throws
	 */
	public List<HashMap<String, Object>> getNoRaceTeam(String matchName,
			int matchType) {
		String sql = "select Distinct(team_name) as teamName from match_order where unit_status=2 and match_name = ? and final_preliminary = ?";
		List<HashMap<String, Object>> data = connSql.selectQuery(sql,
				new Object[] { matchName, matchType });
		return data;
	}

	/**
	 * 
	 * @author liuchao
	 * @version 2014-4-1 上午7:35:58
	 * @Description: 返回暂停比赛队伍的项目
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
	public List<HashMap<String, Object>> getNoRaceCategory(String matchName,
			String team, int matchType) {
		String sql = "select match_category as category from match_order where unit_status=2 and match_name = ? and team_name = ? and final_preliminary = ?";
		List<HashMap<String, Object>> data = connSql.selectQuery(sql,
				new Object[] { matchName, team, matchType });
		return data;
	}

	/**
	 * 
	 * @author liuchao
	 * @version 2014-4-1 上午7:44:48
	 * @Description: 得到暂停比赛的队伍ID
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
	public List<HashMap<String, Object>> getNoRaceTeamID(String matchName,
			int matchType, String team, String category) {
		String sql = "select id as re_id from match_order where unit_status=2 and match_name = ? and final_preliminary = ? and team_name = ? and match_category = ?";
		List<HashMap<String, Object>> data = connSql.selectQuery(sql,
				new Object[] { matchName, matchType, team, category });
		return data;
	}

	/**
	 * @author liuchao
	 * @version 2014-4-3 下午12:14:42
	 * @Description: 得到所有需要进行补赛的赛事名称
	 * @return
	 * @throws
	 */
	public List<HashMap<String, Object>> getAllReplayMatchNames() {
		String sql = "select distinct match_name as matchName from match_order where unit_status = 2";
		List<HashMap<String, Object>> data = connSql.selectQuery(sql,
				new Object[0]);
		return data;
	}

	/**
	 * @author liuchao
	 * @version 2014-3-31 下午11:42:25
	 * @Description: 得到下一只队伍的相关信息
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
		String sql = "select id as id, match_order as matchOrder, match_num as matchNum, match_category as category, match_units as teamName,final_preliminary as final, match_name as matchName from match_order where unit_status=0 and match_num = ? and match_order = ? and match_name=?";
		List<HashMap<String, Object>> data = connSql.selectQuery(sql,
				new Object[] { match_num, matchOrder, matchName });
		return data;
	}
}
