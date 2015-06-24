package com.artisan.dance.application;

import android.app.Application;

/**
* @ClassName: CrashApplication
* @Description: TODO(继承Application,在出现未处理的异常时，调用自定义的全局异常)
* @author czf
* @date 2014-4-20 下午2:08:14
* 
*/
public class CrashApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		CrashHandler crashHandler = CrashHandler.getInstance(getApplicationContext());
	}
}
