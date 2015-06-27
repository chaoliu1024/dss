/*
 * Copyright (c) 2015, NUIST - 120Lib. All rights reserved.
 */

package nuist.qlib.dss.config;

import java.util.HashMap;
import java.util.List;

import nuist.qlib.dss.dao.ConfigDao;

public class Config {
	private ConfigDao dao;

	public Config() {
		dao = new ConfigDao();
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

	/**
	 * 返回裁判长的配置信息
	 * 
	 * @return
	 */
	public List<HashMap<String, Object>> getParams() {
		return dao.getParams();
	}

	/***
	 * 更新裁判长的配置信息
	 * 
	 * @param data
	 * @return
	 */
	public int updateParams(List<HashMap<String, Object>> data) {
		return dao.updateParams(data);
	}
}
