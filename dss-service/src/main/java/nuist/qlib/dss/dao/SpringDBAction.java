package nuist.qlib.dss.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

public class SpringDBAction {
	private DriverManagerDataSource dataSource;
	private Integer showsql;

	private void printsql(String sql, Object... params) {
		if (this.showsql.intValue() > 0)
			if ((params != null) && (params.length > 0)
					&& (this.showsql.intValue() > 1)) {
				StringBuffer sb = new StringBuffer();
				sb.append("[");
				for (int i = 0; i < params.length - 1; ++i) {
					sb.append(params[i]);
					sb.append(",");
				}
				sb.append(params[(params.length - 1)]);
				sb.append("]");
				System.out.println(sql + sb.toString());
			} else {
				System.out.println(sql);
			}
	}

	public List<HashMap<String, Object>> query(String sql, Object... params) {
		List<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			printsql(sql, params);
			pst = this.dataSource.getConnection().prepareStatement(sql);
			for (int i = 1; (params != null) && (i <= params.length); ++i) {
				pst.setObject(i, params[(i - 1)]);
			}
			rs = pst.executeQuery();
			ResultSetMetaData rsd = rs.getMetaData();
			String[] headNames = new String[rsd.getColumnCount()];
			for (int i = 1; i <= headNames.length; ++i) {
				headNames[(i - 1)] = rsd.getColumnName(i);
			}
			while (rs.next()) {
				HashMap<String, Object> map = new HashMap<String, Object>();
				for (int i = 1; i <= headNames.length; ++i) {
					map.put(headNames[(i - 1)], rs.getObject(i));
				}
				list.add(map);
			}
		} catch (Exception e) {
			System.err.println("查询sql执行出错...");
			e.printStackTrace();
		} finally {
			try {
				if ((rs != null) && (!rs.isClosed())) {
					rs.close();
				}
				if ((pst != null) && (!pst.isClosed()))
					pst.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return list;
	}

	public int getCount(String sql, Object... params) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			printsql(sql, params);
			pst = this.dataSource.getConnection().prepareStatement(sql);
			for (int i = 1; (params != null) && (i <= params.length); ++i) {
				pst.setObject(i, params[(i - 1)]);
			}
			rs = pst.executeQuery();
			if (rs.next()) {
				int i = rs.getInt(1);
				return i;
			}
			return 0;
		} catch (Exception e) {
			System.err.println("查询sql执行出错...");
			e.printStackTrace();
		} finally {
			try {
				if ((rs != null) && (!rs.isClosed())) {
					rs.close();
				}
				if ((pst != null) && (!pst.isClosed()))
					pst.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return 0;
	}

	public int update(String sql, Object... params) {
		PreparedStatement pst = null;
		int rs = 0;
		try {
			printsql(sql, params);
			pst = this.dataSource.getConnection().prepareStatement(sql);
			for (int i = 1; (params != null) && (i <= params.length); ++i) {
				pst.setObject(i, params[(i - 1)]);
			}
			rs = pst.executeUpdate();
		} catch (Exception e) {
			rs = -1;
			System.err.println("更新sql执行出错...");
			e.printStackTrace();
		} finally {
			try {
				if ((pst != null) && (!pst.isClosed()))
					pst.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return rs;
	}

	public int updateAll(String[] sql) {
		Connection conn = null;
		Statement stmt = null;
		int rs = 0;
		try {
			conn = this.dataSource.getConnection();
			if (this.showsql.intValue() > 0) {
				for (String sqlstr : sql) {
					System.out.println(sqlstr);
				}
			}
			conn.setAutoCommit(false);
			stmt = conn.createStatement();
			for (String sqlstr : sql) {
				stmt.executeUpdate(sqlstr);
			}
			rs = 1;
			conn.commit();
		} catch (Exception e) {
			try {
				if (conn != null)
					conn.rollback();
			} catch (Exception ee) {
				ee.printStackTrace();
			}
			rs = -1;
			System.err.println("更新sql执行出错...");
			e.printStackTrace();
		} finally {
			try {
				if ((stmt != null) && (!stmt.isClosed()))
					stmt.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return rs;
	}

	public DriverManagerDataSource getDataSource() {
		return this.dataSource;
	}

	public void setDataSource(DriverManagerDataSource dataSource) {
		this.dataSource = dataSource;
	}

	public Integer getShowsql() {
		return this.showsql;
	}

	public void setShowsql(Integer showsql) {
		this.showsql = showsql;
	}
}
