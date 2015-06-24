/**   
 * @Title: OnlineDao.java
 * @Package com.artisan.scoresys.dao
 * @Description: TODO(在角色上线和下线的时候设置角色的状态位)
 * @author czf  
 * @date 2014年4月20日 下午3:55:16
 * @version V1.0   
 */
package nuist.qlib.dss.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;

import org.apache.log4j.Logger;

/**
 * @ClassName: OnlineDao
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author czf
 * @date 2014年4月20日 下午3:55:16
 * 
 */
public class OnlineDao {
	// 连接数据库需要的变量
	private ConnSQL connSql;
	private Logger logger;

	PreparedStatement st;
	Connection conn;

	/**
	 * <p>
	 * Title:
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 */
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
