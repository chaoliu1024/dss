/*
 * Copyright (c) 2015, NUIST - 120Lib. All rights reserved.
 */

package nuist.qlib.dss.dao;

import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * 配置文件(仲裁主任、副裁判长、总裁判长、比赛地点)
 * 
 * @author WangFang
 * @since dss 1.0
 *
 */
public class ConfigDao {
	// 连接数据库需要的变量
	private ConnSQL connSql = new ConnSQL();
	private Logger logger;

	/** 构造函数，读取数据库的配置，同时与数据库建立连接 */
	public ConfigDao() {
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

	/**
	 * 返回配置信息
	 * 
	 * @return
	 */
	public List<HashMap<String, Object>> getParams() {
		String sql = "select id,name,role,location from config order by id";
		return connSql.selectQuery(sql, new Object[0]);
	}

	/***
	 * 更新配置信息
	 * 
	 * @param data
	 * @return
	 */
	public int updateParams(List<HashMap<String, Object>> data) {
		String sql = "update config set name=?,location=? where id=?";
		return connSql.insertBatch(data, sql, new String[] { "name",
				"location", "id" });
	}
}
