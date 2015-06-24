/*
 * 文件名：LoginActivity.java
 * 版权：Copyright 2014 Artisan WangFang
 * 描述：登陆Activity
 */
package com.artisan.dance.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.artisan.dance.adapter.KindRolesAdapter;
import com.artisan.dance.adapter.RolesAdapter;
import com.artisan.dance.dao.LoginDao;
import com.artisan.dance.service.BroadcastIPService;
import com.artisan.dance.service.ReceiveInforService;
import com.artisan.dance.ui.ProgressBackDialog;
import com.artisan.dance.util.ViewHolder;

public class LoginActivity extends Activity {
	private Spinner spinner;
	private List<HashMap<String, Object>> roles;
	private String error;
	public static String role = "";
	public static String roleName = "";
	private List<HashMap<String, Object>> kinds;
	private ProgressDialog dialog;
	private ProgressDialog dialogSubmit;
	private HandlerThread threadt;
	private String roleDes;
	private RolesAdapter adapter;
	private KindRolesAdapter kindRolesAdapter;
	private ListView listView;
	public static Intent BroadcastIPIntent = null;
	private static Intent ReceiveInforIntent = null;
	private int i = -1; // 线程次数
	private List<Handler> handlers = new ArrayList<Handler>();
	private MyHandler handler;
	private Handler ulHandler;
	private Runnable mBackgroundRunnable;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.login_layout);

		spinner = (Spinner) findViewById(R.id.rolespinner);// 选择登陆角色
		kinds = new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> one = new HashMap<String, Object>();
		one.put("name", "请选择");
		one.put("value", "");
		kinds.add(one);
		one = new HashMap<String, Object>();
		one.put("name", "打分裁判");
		one.put("value", "1");
		kinds.add(one);
//		one = new HashMap<String, Object>();
//		one.put("name", "裁判长");
//		one.put("value", "0");
//		kinds.add(one);
		adapter = new RolesAdapter(LoginActivity.this, kinds);
		spinner.setAdapter(adapter);
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				if (!kinds.get(arg2).get("value").toString().equals("")) {
					roleDes = kinds.get(arg2).get("value").toString();
					create_spinnerData(roleDes);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}

		});
		ulHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				switch (msg.what) {
				case 1:
					View view = LayoutInflater.from(LoginActivity.this)
							.inflate(R.layout.roles_dialog, null);
					listView = (ListView) view.findViewById(R.id.roles);
					kindRolesAdapter = new KindRolesAdapter(LoginActivity.this,
							roles);
					listView.setAdapter(kindRolesAdapter);
					spinner.setSelection(0, true);
					listView.setOnItemClickListener(new OnItemClickListener() {
						@Override
						public void onItemClick(AdapterView<?> arg0, View arg1,
								int arg2, long arg3) {
							// TODO Auto-generated method stub
							ViewHolder holder;
							for (int i = 0; i < listView.getChildCount(); i++) {
								holder = (ViewHolder) listView.getChildAt(i)
										.getTag();
								holder.radio.setChecked(false);
							}
							holder = (ViewHolder) listView.getChildAt(arg2)
									.getTag();
							holder.radio.setChecked(true);
							role = roles.get(arg2).get("value").toString();
							roleName=roles.get(arg2).get("name").toString();
						}

					});
					new AlertDialog.Builder(LoginActivity.this)
							.setTitle("角色选择")
							.setView(listView)
							.setPositiveButton("登陆",
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											// TODO Auto-generated method stub
											if (role.equals("")) {
												Toast.makeText(
														LoginActivity.this,
														"请选择角色",
														Toast.LENGTH_SHORT)
														.show();
											} else
												submit();
										}
									})
							.setNegativeButton("取消",
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											// TODO Auto-generated method stub
											dialog.dismiss();
										}
									}).show();
				}
			}
		};
	}

	/***
	 * 初始化对应角色，如 裁判长和打分裁判
	 */
	public void create_spinnerData(String kind) {
		
		if(this!=null){
			dialog =new ProgressBackDialog(this).getProgressDialog("请稍候", "正在加载所有角色...");
		}

		dialog.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				// TODO Auto-generated method stub
				handler.setStop(true);
				spinner.setSelection(0, true);
			}
		});
		dialog.show();
		i++;
		threadt = new HandlerThread("post" + i);
		threadt.start();

		mBackgroundRunnable = new Runnable() {
			public void run() {
				int index = i;
				LoginDao dao = new LoginDao(LoginActivity.this.getFilesDir());
				if (dao.checkConfig()) {
					HashMap<String, Object> result = dao.getRoles(roleDes);
					if (result.get("message").equals("success")) {
						roles = (List<HashMap<String, Object>>) result
								.get("data");
						handlers.get(index).sendEmptyMessage(1);
					} else {
						error = result.get("message").toString();
						handlers.get(index).sendEmptyMessage(2);
					}
				} else {
					handlers.get(index).sendEmptyMessage(0);
				}
			}
		};
		handler = new MyHandler(threadt.getLooper());
		handler.setMark(0);
		handlers.add(handler);
		handler.post(mBackgroundRunnable);
	}

	public void submit() {
		adapter.notifyDataSetChanged();
		i++;
		threadt = new HandlerThread("post" + i);
		threadt.start();

		mBackgroundRunnable = new Runnable() {
			@Override
			public void run() {
				int index = i;
				LoginDao dao = new LoginDao(LoginActivity.this.getFilesDir());
				if (dao.checkConfig()) {
					HashMap<String, Object> result = dao.roleCheck(role);
					if (result.get("message").equals("success")) {
						if (result.get("result").equals("true")) { // 登陆成功
							handlers.get(index).sendEmptyMessage(1);
						} else if (result.get("result").equals("false")) { // 登陆失败
							handlers.get(index).sendEmptyMessage(4);
						} else if (result.get("result").equals("fail")) { // 已经有人登陆
							handlers.get(index).sendEmptyMessage(3);
						}
					} else {
						error = result.get("message").toString();
						handlers.get(index).sendEmptyMessage(2);
					}
				} else {
					handler.sendEmptyMessage(0);
				}
			}
		};
		handler = new MyHandler(threadt.getLooper());
		handler.setMark(1);
		handlers.add(handler);
		handler.post(mBackgroundRunnable);
		if(LoginActivity.this!=null){
			dialogSubmit = ProgressDialog.show(LoginActivity.this, "请稍候...",
					"正在验证身份中...", true);
		}
	}

	/**
	 * 菜单、返回键响应
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK
				|| event.getAction() == KeyEvent.ACTION_DOWN) {

			new AlertDialog.Builder(LoginActivity.this)
					.setTitle("提示信息")
					.setMessage("确定退出？")
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									dialog.dismiss();
									closeService();
									System.exit(0);
								}
							})
					.setNegativeButton("取消",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {

									dialog.dismiss();
								}
							}).show();
		}
		return false;
	}

	public void closeService() {
		if (SplashActivity.ReceiveIPIntent != null) {
			stopService(SplashActivity.ReceiveIPIntent); // 停止接收IP的服务
		}
		if (ReceiveInforIntent != null) {
			stopService(LoginActivity.ReceiveInforIntent); // 停止接收信息的服务
		}
		System.exit(0);
	}


	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	private class MyHandler extends Handler {
		private boolean isStop = false;
		private int mark = 0; // 0初始化角色，1提交

		public boolean isStop() {
			return isStop;
		}

		public void setStop(boolean isStop) {
			this.isStop = isStop;
		}

		public int getMark() {
			return mark;
		}

		public void setMark(int mark) {
			this.mark = mark;
		}

		public MyHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {

			if (isStop)
				return;
			switch (mark) {
			case 1:				
				switch (msg.what) {
				case 0:
					dialogSubmit.dismiss();
					Toast.makeText(LoginActivity.this, "网络不畅通，请检查",
							Toast.LENGTH_SHORT).show();
					break;
				case 1:
					dialogSubmit.dismiss();
					if (roleDes.equals("1")) {   //打分裁判
						Intent intent = new Intent(LoginActivity.this,
								ScoreActivity.class);
						LoginActivity.this.startActivity(intent);
					} 
					// 启动广播ip的服务
					BroadcastIPIntent = new Intent(LoginActivity.this,
							BroadcastIPService.class);
					startService(BroadcastIPIntent);
					// 启动接收参赛队伍及比赛项目的服务
					ReceiveInforIntent = new Intent(LoginActivity.this,
							ReceiveInforService.class);
					startService(ReceiveInforIntent);
					break;
				case 2:
					dialogSubmit.dismiss();
					if (error.contains("refused")) {
						Toast.makeText(LoginActivity.this, "网络不畅通，请稍候尝试",
								Toast.LENGTH_SHORT).show();
					} else
						Toast.makeText(LoginActivity.this, error,
								Toast.LENGTH_SHORT).show();
					break;
				case 3:
					dialogSubmit.dismiss();
					Toast.makeText(LoginActivity.this, "未知原因，请联系开发人员",
							Toast.LENGTH_LONG).show();
					break;
				case 4:
					dialogSubmit.dismiss();
					Toast.makeText(LoginActivity.this, "已经有人登陆，请重新登陆",
							Toast.LENGTH_LONG).show();
					break;
				}
				break;
			case 0:
				switch (msg.what) {
				case 0:
					dialog.dismiss();
					Toast.makeText(LoginActivity.this, "网络不畅通，请检查",
							Toast.LENGTH_LONG).show();
					break;
				case 1:
					dialog.dismiss();
					ulHandler.sendEmptyMessage(1);
					break;
				case 2:
					dialog.dismiss();
					if (error.contains("refused")) {
						Toast.makeText(LoginActivity.this, "网络不畅通，请稍候尝试",
								Toast.LENGTH_LONG).show();
					} else {
						Toast.makeText(LoginActivity.this, error,
								Toast.LENGTH_LONG).show();
					}
					break;
				}
			}
		}
	}
	@Override
	public void finish() {
		// TODO Auto-generated method stub
		if(dialog.isShowing()){
			dialog.dismiss();
		}
		if(dialogSubmit.isShowing()){
			dialogSubmit.dismiss();
		}
		super.finish();
	}

}
