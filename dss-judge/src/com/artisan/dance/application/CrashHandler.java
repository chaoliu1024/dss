package com.artisan.dance.application;

import java.lang.Thread.UncaughtExceptionHandler;

import android.content.Context;
import android.os.Looper;
import android.widget.Toast;

import com.artisan.dance.activity.LoginActivity;
import com.artisan.dance.activity.SplashActivity;

/**
* @ClassName: CrashHandler
* @Description: TODO(自定义未处理异常的异常处理)
* @author czf
* @date 2014-4-20 下午2:13:40
* 
*/
public class CrashHandler implements UncaughtExceptionHandler {

	public static final String TAG = "CrashHandler";

	// 系统默认的UncaughtException处理类
	private Thread.UncaughtExceptionHandler mDefaultHandler;
	// CrashHandler实例
	private static CrashHandler INSTANCE;
	// 程序的Context对象
	private Context mContext;

	/**
	* @Title: getInstance
	* @Description: TODO(获取CrashHandler实例 ,单例模式)
	* @return
	*/
	public static CrashHandler getInstance(Context context) {
		if(INSTANCE == null){
			INSTANCE = new CrashHandler(context);
			return INSTANCE;
		}else{
			return INSTANCE;
		}		
	}

	/**
	* <p>Title: </p>
	* <p>Description: 初始化</p>
	* @param context
	*/
	public CrashHandler(Context context) {
		mContext = context;
		// 获取系统默认的UncaughtException处理器
		mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
		// 设置该CrashHandler为程序的默认处理器
		Thread.setDefaultUncaughtExceptionHandler(this);
	}

	/* (非 Javadoc)
	* <p>Title: uncaughtException</p>
	* <p>Description:当UncaughtException发生时会转入该函数来处理 </p>
	* @param thread
	* @param ex
	* @see java.lang.Thread.UncaughtExceptionHandler#uncaughtException(java.lang.Thread, java.lang.Throwable)
	*/
	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		if (!handleException(ex) && mDefaultHandler != null) {
			// 如果用户没有处理则让系统默认的异常处理器来处理
			mDefaultHandler.uncaughtException(thread, ex);
		} else {
			// 退出程序
			System.out.println("不好意思啊！红姐。。。有报错了");
			
			android.os.Process.killProcess(android.os.Process.myPid());
			System.exit(0);
		}
	}

	/**
	* @Title: handleException
	* @Description: TODO(自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.)
	* @param ex
	* @return
	*/
	private boolean handleException(Throwable ex) {
		if (ex == null) {
			return false;
		}
		// 使用Toast来显示异常信息
		new Thread() {
			@Override
			public void run() {
				Looper.prepare();
				Toast.makeText(mContext, "很抱歉,程序出现异常,即将退出", Toast.LENGTH_LONG)
						.show();
				Looper.loop();
			}
		}.start();
		return true;
	}
}
