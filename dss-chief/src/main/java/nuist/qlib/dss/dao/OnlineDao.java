/*
 * Copyright (c) 2015, NUIST - 120Lib. All rights reserved.
 */
package nuist.qlib.dss.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;

import org.apache.log4j.Logger;

/**
 * @author czf
 * @since dss 1.0
 */
public class OnlineDao {
	// 连接数据库需要的变量
	private ConnSQL connSql;
	private Logger logger;

	PreparedStatement st;
	Connection conn;

	public OnlineDao() {
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

	public boolean updateLoginStatus(String roleName, boolean status) {
		String sql = "update role set login_state = ? where role_name = ?";
		int result = connSql.updateObject(sql,
				new Object[] { status, roleName });
		if (result == 1) {
			return true;
		} else {
			return false;
		}
	}
}
