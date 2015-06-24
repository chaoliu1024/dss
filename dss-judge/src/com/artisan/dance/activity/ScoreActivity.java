/*
 * 文件名:ScoreActivity.java
 * 版权：Copyright 2014 陈正飞
 * 描述：打分界面
 */
package com.artisan.dance.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.FutureTask;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.artisan.dance.dao.IPDao;
import com.artisan.dance.dao.LoginDao;
import com.artisan.dance.net.SendMessage;

public class ScoreActivity extends Activity {

	private static final String TAG = "ScoreActivity";
	private File path;
	private MyReceiver receiver = null;
	public static IntentFilter filter;
	private String scoreValue;
	private TextView units_content;
	private TextView category_content;
	private TextView role_content;
	private TextView state_content;
	private TextView score;
	private Button send;
	private IPDao ipDao;
	private HandlerThread threadt;
	private ProgressDialog loginOutdialog;
	private boolean login = true;
	private String role;
	private String roleName;
	private String error;
	private int i = -1; // 线程次数
	private List<Handler> handlers = new ArrayList<Handler>();
	private MyHandler handler;
	private Runnable mBackgroundRunnable;
	private Handler sendScoreHandler;
	private String eL = "^[0-1](\\.\\d{0,2})?|2(\\.0{0,2})?$";//非负浮点数 ,且范围在[0,2]
	private String eL2 = "^[0-3](\\.\\d{0,2})?|4(\\.0{0,2})?$";//非负浮点数 ,且范围在[0,4]
	private Pattern pattern = Pattern.compile(eL);
    private Pattern pattern2 = Pattern.compile(eL2);

	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		
		// 无标题模式
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		role = LoginActivity.role;
		roleName=LoginActivity.roleName;
		
		if(role.contains("art")){
			setContentView(R.layout.art_score);
		} else if(role.contains("exec")){
			setContentView(R.layout.exec_score);
		} else{
			setContentView(R.layout.imp_score);
		}
		
		units_content = (TextView) this.findViewById(R.id.units_content);
		category_content = (TextView) this.findViewById(R.id.category_content);
		role_content = (TextView) this.findViewById(R.id.role_content); // 身份信息
		state_content = (TextView) this.findViewById(R.id.state_content); // 当前状态信息
		
		score = (TextView) this.findViewById(R.id.score); //裁判打分
		send = (Button) this.findViewById(R.id.send); //发送按钮
		path = this.getFilesDir();
		
		role_content.setText(roleName); // 设置身份
		state_content.setText("未打分"); // 初始化状态

		// 接收服务发过来的参赛队伍和比赛项目信息
		if (receiver == null) {
			receiver = new MyReceiver();
			filter = new IntentFilter();
			filter.addAction("android.intent.action.MY_RECEIVER");
			this.registerReceiver(receiver, filter); // 动态注册广播信息
		}	
		
		send.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub	
				scoreValue = score.getText().toString();
				if(role.contains("imp")){
					if(!pattern.matcher(scoreValue).matches()){//打分不符合要求
						Toast.makeText(ScoreActivity.this,
								"请确保打分范围为0~2,且最多包含两位小数！", Toast.LENGTH_SHORT)
								.show(); // 提示打分超出范围
						return;
					}					
				} else {
					if(!pattern2.matcher(scoreValue).matches()){
						Toast.makeText(ScoreActivity.this,
								"请确保打分范围为0~4,且最多包含两位小数！", Toast.LENGTH_SHORT)
								.show(); // 提示打分超出范围
						return;
					}
				}
				sendScoreHandler = new sendScoreHandler();
				new Thread() {
					public void run() {
						try {
							ipDao = new IPDao(path);
							List<String> list = new ArrayList<String>();
							String receiver[] = { "Editor" }; // 接收者名称(裁判长和记录员)
							list = ipDao.getIP(receiver); // 获取IP
							List<FutureTask<Integer>> tasks = new ArrayList<FutureTask<Integer>>();
							int sum = 1;

							// 遍历获取的IP，并发送
							if(list.size() == 0){//未获得ip
								sum = 0;
							} else{
								for (int i = 0; i < list.size(); i++) {
									FutureTask<Integer> task = new FutureTask<Integer>(
											new SendMessage(list.get(i),scoreValue));
									tasks.add(task);
									new Thread(task).start();
								}

								for (FutureTask<Integer> task : tasks)
									// 获取线程返回值
									sum *= task.get();
							}

							// 发送消息
							Message message = new Message();
							// 发送消息与处理函数里一致
							message.what = sum;
							// 内部类调用外部类的变量
							sendScoreHandler.sendMessage(message);
						} catch (Exception e) {
							Log.e(TAG, e.getMessage());
						}
					}
				}.start();
			}
		});
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		
		if (LoginActivity.BroadcastIPIntent != null) {
			stopService(LoginActivity.BroadcastIPIntent); // 停止组播IP的服务
		}
		if (receiver != null) {
			unregisterReceiver(receiver);
			receiver = null;
		}
		super.onDestroy();
	}

	/**
	 * 广播接收器
	 */
	private class MyReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			Bundle bundle = intent.getExtras();
			String item = bundle.getString("item");
			if (item.equalsIgnoreCase("infor2")) { // 接收队伍信息
				units_content.setText(bundle.getString("team_name"));
				category_content.setText(bundle.getString("category_name"));
				score.setEnabled(true);
				send.setEnabled(true);
				score.setText("");			
				state_content.setText("未打分");
			}else if(item.equalsIgnoreCase("Command")){
				if(bundle.getString("CommandContent").equals("up")){
					state_content.setText("需调分");
					ImageView img = new ImageView(ScoreActivity.this);
					img.setImageResource(R.drawable.up);
					new AlertDialog.Builder(ScoreActivity.this)
					.setTitle("提示")
					.setView(img)
					.setPositiveButton("确定", null)
					.show();
				}else if(bundle.getString("CommandContent").equals("down")){
					state_content.setText("需调分");
					ImageView img = new ImageView(ScoreActivity.this);
					img.setImageResource(R.drawable.down);
					new AlertDialog.Builder(ScoreActivity.this)
					.setTitle("提示")
					.setView(img)
					.setPositiveButton("确定", null)
					.show();
				}else{
					state_content.setText("已打分");
					ImageView img = new ImageView(ScoreActivity.this);
					img.setImageResource(R.drawable.ok);
					new AlertDialog.Builder(ScoreActivity.this)
					.setTitle("提示")
					.setView(img)
					.setPositiveButton("确定", null)
					.show();
					return;
				}
				score.setEnabled(true);
				send.setEnabled(true);				
			}
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			new AlertDialog.Builder(ScoreActivity.this)
					.setTitle("提示")
					.setMessage("确定退出打分界面?")
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									dialog.dismiss();
									loginOutdialog = getProgressDialog(
											"请稍候...", "正在退出登陆...");
									loginOutdialog.show();
									i++;
									threadt = new HandlerThread("loginout" + i);
									threadt.start();
									mBackgroundRunnable = new Runnable() {
										@Override
										public void run() {
											int index = i;
											LoginDao dao = new LoginDao(
													ScoreActivity.this
															.getFilesDir());
											if (dao.checkConfig()) {
												HashMap<String, Object> result = dao
														.loginOut(role);
												if (result.get("message")
														.equals("success")) {
													if (result.get("result")
															.equals("true")) {
														handlers.get(index)
																.sendEmptyMessage(
																		1);
													} else {
														handlers.get(index)
																.sendEmptyMessage(
																		2);
													}
												} else {
													error = result.get(
															"message")
															.toString();
													handlers.get(index)
															.sendEmptyMessage(3);
												}

											} else {
												handlers.get(index)
														.sendEmptyMessage(0);
											}
										}
									};
									handler = new MyHandler(threadt.getLooper());
									handler.post(mBackgroundRunnable);
									handlers.add(handler);
								}

							})
					.setNegativeButton("取消",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									dialog.dismiss();
								}
							}).show();
			return false;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}
	
	/*
	 * 打分之后更新UI
	 */
	private class sendScoreHandler extends Handler {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0: 
				score.setEnabled(true);
				send.setEnabled(true);
				score.setText(""); 
				state_content.setText("未打分");
				new AlertDialog.Builder(ScoreActivity.this)
						.setTitle("提示")
						.setMessage("未获得接收地址，请休息片刻！")
						.setPositiveButton("确定", null)
						.show(); // 提示发送失败
				break;
			case 1:
				score.setEnabled(false);
				send.setEnabled(false);
				state_content.setText("已打分");
				Toast.makeText(ScoreActivity.this, "打分成功",
						Toast.LENGTH_SHORT).show(); // 提示发送成功
				break;
			case -1:
				ipDao.clearIP();// 清空配置文件
				score.setEnabled(true);
				send.setEnabled(true);
				score.setText(""); // 发送完清空内容
				state_content.setText("未打分");
				new AlertDialog.Builder(ScoreActivity.this)
						.setTitle("提示")
						.setMessage("发送失败，请重发")
						.setPositiveButton("确定", null)
						.show(); // 提示发送失败
				break;
			}
			super.handleMessage(msg);
		}
	}; 
	
	private class MyHandler extends Handler {
		private boolean isStop = false;

		public boolean isStop() {
			return isStop;
		}

		public void setStop(boolean isStop) {
			this.isStop = isStop;
		}

		public MyHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {
			if (isStop)
				return;
			loginOutdialog.dismiss();
			switch (msg.what) {
			case 0: // 数据库配置错误
				Toast.makeText(ScoreActivity.this, "网络不畅通，请检查",
						Toast.LENGTH_SHORT).show();
				login = true;
				break;
			case 1: // 成功退出
				login = false;
				break;
			case 2: // 服务器端退出异常
				Toast.makeText(ScoreActivity.this, "退出登陆不成功",
						Toast.LENGTH_SHORT).show();

				login = true;
				break;
			case 3: // 连接服务器端异常
				// System.out.println("退出异常："+error);
				login = true;
				break;
			}

			if (login) {
				String message = "";
				if (error != null && error.contains("refused")) {
					message = "网络不畅通，请稍候尝试";
				} else {
					message = "服务器发生异常，无法退出，";
				}
				new AlertDialog.Builder(ScoreActivity.this)
						.setTitle("提示信息")
						.setMessage(message + "仍然要退出程序吗？")
						.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// TODO Auto-generated method stub
										if (LoginActivity.BroadcastIPIntent != null) {
											stopService(LoginActivity.BroadcastIPIntent); // 停止组播IP的服务
										}
										if (receiver != null) {
											unregisterReceiver(receiver);
											receiver = null;
										}
										finish();
									}
								})
						.setNegativeButton("取消",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// TODO Auto-generated method stub
										dialog.dismiss();
									}
								}).show();
			} else {
				if (LoginActivity.BroadcastIPIntent != null) {
					stopService(LoginActivity.BroadcastIPIntent); // 停止组播IP的服务
				}
				if (receiver != null) {
					unregisterReceiver(receiver);
					receiver = null;
				}
				finish();
			}
		}
	}

	public ProgressDialog getProgressDialog(String title, String msg) {
		ProgressDialog progressDialog = new ProgressDialog(this);
		progressDialog.setTitle(title);
		progressDialog.setIndeterminate(true);
		progressDialog.setMessage(msg);
		progressDialog.setCancelable(true);
		progressDialog.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				// TODO Auto-generated method stub
				handler.setStop(true);
			}

		});
		return progressDialog;

	}
}
