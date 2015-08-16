/*
 * Copyright (c) 2015, NUIST - 120Lib. All rights reserved.
 */

package nuist.qlib.dss.dao;

import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConnSQL {
	private String ip;
	private String port;
	private String baseName;
	private String userName;
	private String userPwd;
	private Logger logger;
	private PreparedStatement st;
	private Connection conn;

	public ConnSQL() {
		logger = LoggerFactory.getLogger(ConnSQL.class);
		Properties props = new Properties();
		try {
			String relativelyPath = System.getProperty("user.dir");
			InputStream in = new FileInputStream(relativelyPath
					+ "\\dataBase.properties");
			props.load(in);
			port = props.getProperty("port");
			ip = props.getProperty("ip");
			baseName = props.getProperty("baseName");
			userName = props.getProperty("userName");
			userPwd = props.getProperty("userPwd");
			conn = connectDataBase();
		} catch (Exception e) {
			logger.error(e.toString());
			e.printStackTrace();
		}
	}

	/** 连接数据库 */
	public Connection connectDataBase() {
		Connection conn = null;
		if (ip == null || ip.equals("") || port == null || port.equals("")
				|| baseName == null || baseName.equals("") || userName == null
				|| userName.equals("") || userPwd == null || userPwd.equals("")) {
			return conn;
		} else {
			try {
				String url = "jdbc:sqlserver://" + ip + ":" + port
						+ ";DatabaseName=" + baseName;
				conn = DriverManager.getConnection(url, userName, userPwd);
				return conn;
			} catch (Exception e) {
				logger.error(e.getMessage());
				e.printStackTrace();
				return conn;
			}
		}
	}

	/** 判断是否正确连接数据库 */
	public boolean isConnected() {
		if (conn == null) {
			return false;
		} else
			return true;
	}

	/***
	 * 删除操作
	 * 
	 * @param sql
	 * @param params
	 * @return
	 */
	public boolean deleteObject(String sql, Object[] params) {
		try {
			st = conn.prepareStatement(sql);
			for (int i = 1; i <= params.length && params.length != 0; i++) {
				st.setObject(i, params[i - 1]);
			}
			st.execute();
			return true;
		} catch (SQLException e) {
			logger.error(e.toString());
			e.printStackTrace();
			return false;
		}
	}

	/***
	 * 批量插入数据或者批量更新数据
	 * 
	 * @param data
	 * @param sql
	 * @param keys
	 * @return 返回-1表示插入失败
	 */
	public int insertBatch(List<HashMap<String, Object>> data, String sql,
			String[] keys) {
		int result = 1;
		try {
			conn.setAutoCommit(false);
			st = conn.prepareStatement(sql);
			for (int i = 0; i < data.size(); i++) {
				for (int j = 0; j < keys.length; j++) {
					st.setObject(j + 1, data.get(i).get(keys[j]));
				}
				st.addBatch();
				if (i > 1000) {
					int[] results = st.executeBatch();
					for (int k : results) {
						if (k < 0) {
							result = -1;
						}
					}
				}
			}
			int[] results = st.executeBatch();
			for (int k : results) {
				if (k < 0) {
					result = -1;
				}
			}
			conn.commit();
			conn.setAutoCommit(true);
		} catch (Exception e) {
			logger.error(e.toString());
			e.printStackTrace();
			result = -1;
		}
		return result;
	}

	/***
	 * 插入一条数据
	 * 
	 * @param sql
	 * @param params
	 * @return 返回为-1时，表示插入失败，否则插入成功
	 */
	public int insertObject(String sql, Object[] params) {
		int result = 1;
		try {
			st = conn.prepareStatement(sql);
			for (int i = 0; i < params.length; i++) {
				st.setObject(i + 1, params[i]);
			}
			if (st.executeUpdate() > 0) {
				result = 1;
			} else
				result = -1;
		} catch (Exception e) {
			logger.error(e.toString());
			e.printStackTrace();
			result = -1;
		}
		return result;
	}

	/***
	 * 查询数据
	 * 
	 * @param sql
	 * @param params
	 * @return
	 */
	public List<HashMap<String, Object>> selectQuery(String sql, Object[] params) {
		List<HashMap<String, Object>> result = new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> one = null;
		try {
			st = conn.prepareStatement(sql);
			for (int i = 0; i < params.length; i++) {
				st.setObject(i + 1, params[i]);
			}
			ResultSet rs = st.executeQuery();
			ResultSetMetaData meta = rs.getMetaData();
			while (rs.next()) {
				one = new HashMap<String, Object>();
				for (int i = 1; i <= meta.getColumnCount(); i++) {
					one.put(meta.getColumnLabel(i), rs.getObject(i));
				}
				result.add(one);
			}
			rs.close();
		} catch (Exception e) {
			logger.error(e.toString());
			e.printStackTrace();
		}
		return result;
	}

	/***
	 * 插入一条数据
	 * 
	 * @param sql
	 * @param params
	 * @return 返回为-1时，表示插入失败，否则插入成功
	 */
	public int updateObject(String sql, Object[] params) {
		int result = 1;
		try {
			st = conn.prepareStatement(sql);
			for (int i = 0; i < params.length; i++) {
				st.setObject(i + 1, params[i]);
			}
			if (st.executeUpdate() != 0) {
				result = 1;
			} else
				result = -1;
		} catch (Exception e) {
			logger.error(e.toString());
			e.printStackTrace();
			result = -1;
		}
		return result;
	}

	public void close() {
		try {
			if (st != null && !st.isClosed()) {
				st.close();
			}
			if (conn != null && !conn.isClosed()) {
				conn.close();
			}
		} catch (SQLException e) {
			logger.error(e.toString());
			e.printStackTrace();
		}
	}

	public Connection getConn() {
		return conn;
	}
}