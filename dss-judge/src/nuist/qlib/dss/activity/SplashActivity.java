/*
 * 文件名:SplashActivity.java
 * 版权：Copyright 2013 刘超
 * 描述：程序开启画面
 */
package nuist.qlib.dss.activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.Properties;

import nuist.qlib.dss.service.ReceiveIPService;
import nuist.qlib.dss.util.ToolUtil;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.provider.Settings;
import android.view.Window;
import android.view.WindowManager;

public class SplashActivity extends Activity {
	
	public static Intent ReceiveIPIntent;
	private ProgressDialog dialog;
	private HandlerThread thread;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// 设置为无标题栏
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		// 设置为全屏模式
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.splash);
		checkNetworkInfo();

	}

	/***
	 * 检测网络连接状态
	 */
	public void checkNetworkInfo() {
		ConnectivityManager conMan = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		State wifi = conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
				.getState(); // wifi连接状态

		if (wifi == State.CONNECTED) {// 如果wifi网络已连接，或处于正在连接状态 // ，则进入登录界面
			// 启动接收ip的服务
			ReceiveIPIntent = new Intent(SplashActivity.this,
					ReceiveIPService.class);
			startService(ReceiveIPIntent);

			 ToolUtil.removeDBConfig(SplashActivity.this.getFilesDir());
//			ToolUtil.createDBConfig(SplashActivity.this.getFilesDir(),"192.168.0.105");
			
			// 加载登陆角色
			dialog = ProgressDialog.show(SplashActivity.this, "请稍候...",
					"努力加载中...", true);
			final Handler handler = new Handler() {
				@Override
				public void handleMessage(Message msg) {
					// TODO Auto-generated method stub
					switch (msg.what) {
					case 0:// 加载成功
						dialog.dismiss();
						Intent mainIntent = new Intent(SplashActivity.this,
								LoginActivity.class);
						SplashActivity.this.startActivity(mainIntent);
						SplashActivity.this.finish();
						break;
					case 1:// 加载失败
						dialog.dismiss();
						if (ReceiveIPIntent != null) {
							SplashActivity.this.stopService(ReceiveIPIntent); // 关闭服务
						}
						new AlertDialog.Builder(SplashActivity.this)
								.setTitle("提示")
								.setMessage("网络不畅通，请检查网络")
								.setPositiveButton("关闭",
										new DialogInterface.OnClickListener() {
											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {
												// TODO Auto-generated method
												// stub
												dialog.dismiss();
												SplashActivity.this.finish();
												System.exit(0);
											}
										}).show();
					}
				}
			};
			thread = new HandlerThread("post") {
				public void run() {
					// TODO Auto-generated method stub
					Date date = new Date();
					while (true) {
						Date temp = new Date();
						if (temp.getTime() - date.getTime() > 60000) {
							handler.sendEmptyMessage(1);
							break;
						} else {
							Properties props = new Properties();
							File file = new File(
									SplashActivity.this.getFilesDir(),
									"dbConfig.properties");
							try {
								InputStream in = new FileInputStream(file);
								props.load(in);
							} catch (Exception e) {
								e.printStackTrace();
							}
							String ip = props.getProperty("ip");
							// System.out.println("继续:" + ip);
							if (ip == null || ip.equals("")) {
								try {
									this.sleep(4000);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								// continue;
							} else {
								System.out.println(ip + "....");
								handler.sendEmptyMessage(0);
								break;
							}
						}
					}
				}
			};
			thread.start();
			ManageApplication.getInstance().addActivity(this);
		} else {// 如果wifi网络未连接，且不是处于正在连接状态 则进入Network Setting界面 由用户配置网络连接
			new AlertDialog.Builder(SplashActivity.this)
					.setTitle("提示信息")
					.setMessage("网络未连接，请连接网络后重新登录")
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									startActivity(new Intent(
											Settings.ACTION_WIFI_SETTINGS)); // 进入wifi网络设置界面
									finish();
								}
							}).show();
		}
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub
		if (dialog != null && dialog.isShowing()) {
			dialog.dismiss();
		}
		if (thread != null && thread.isAlive()) {
			thread.quit();
		}
		super.finish();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if (dialog != null && dialog.isShowing()) {
			dialog.dismiss();
		}
		if (thread != null && thread.isAlive()) {
			thread.quit();
		}
		super.onDestroy();
	}
}
