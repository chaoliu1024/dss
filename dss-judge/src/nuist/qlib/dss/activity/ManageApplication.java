/*
 * 文件名:ManageApplication.java
 * 版权：Copyright 2013 刘超
 * 描述：管理所有Activity,便于结束程序
 */

package nuist.qlib.dss.activity;

import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.app.Application;

public class ManageApplication extends Application {

	private List<Activity> activityList = new LinkedList<Activity>();
	private static ManageApplication instance;

	private ManageApplication() {
	}
	
	/**
	 * 单例模式中获取唯一的Application实例
	 * @author liuchao
	 * @version 1.0 2013.09.09
	 * @return
	 */
	public static ManageApplication getInstance() {
		if (null == instance) {
			instance = new ManageApplication();
		}
		return instance;
	}

	/**
	 * 添加Activity到容器中
	 * @author liuchao
	 * @version 1.0 2013.09.09
	 * @param activity
	 */
	public void addActivity(Activity activity) {
		activityList.add(activity);
	}

	/**
	 * 遍历所有Activity并finish
	 * @author liuchao
	 * @version 1.0 2013.09.09
	 */
	public void exit() {
		for (Activity activity : activityList) {
			activity.finish();
		}
		System.exit(0);
	}
}