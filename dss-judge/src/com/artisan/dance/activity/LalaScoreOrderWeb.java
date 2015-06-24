/*
 * 文件名：LalaScoreOrderWeb.java
 * 版权：Copyright 2014 Artisan WangFang
 * 描述：分数查询页面
 */
package com.artisan.dance.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.artisan.dance.adapter.ScoreQueryAdapter;
import com.artisan.dance.dao.LalaScoreDAO;
import com.artisan.dance.ui.ProgressBackDialog;

public class LalaScoreOrderWeb extends Activity {
	private ListView mListView1;
	private ScoreQueryAdapter myAdapter;
	private RelativeLayout mHead;
	private String matchName = ""; // 赛事名称
	private String matchCategory = "项目1"; // 比赛项目
	private int matchType = 0; // 判断是决赛还是预赛
	private Spinner catoryView;
	private List<HashMap<String, Object>> data;
	private ArrayAdapter<String> adapter;
	private List<String> catoryData;
	private Button query;
	private MyHandler handler;
	private Runnable mBackgroundRunnable;
	private HandlerThread thread;
	private int mark = 0; // 用在线程中用
	private String error;
	private ProgressDialog dialog;
	private int i = -1; // 线程次数
	private List<Handler> handlers = new ArrayList<Handler>();
	private Handler uiHandler; // 更新界面中的内容

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// 无标题模式
		requestWindowFeature(Window.FEATURE_NO_TITLE);
				
		Intent intent = this.getIntent();
		matchName = intent.getStringExtra("matchName");
		matchCategory = intent.getStringExtra("matchCategory");
		matchType = intent.getIntExtra("matchType", -1);
		this.setContentView(R.layout.activity_score_order_main);

		TextView matchNameView = (TextView) this.findViewById(R.id.matchName);
		matchNameView.setText(matchName);
		catoryView = (Spinner) this
				.findViewById(R.id.category);
		query = (Button) this.findViewById(R.id.query);
		
		mHead = (RelativeLayout) findViewById(R.id.skill_head);
		mHead.setFocusable(true);
		mHead.setClickable(true);
		mHead.setBackgroundColor(Color.parseColor("#b2d235"));
		mHead.setOnTouchListener(new ListViewAndHeadViewTouchLinstener());

		mListView1 = (ListView) findViewById(R.id.listView1);
		mListView1.setOnTouchListener(new ListViewAndHeadViewTouchLinstener());
		data = new ArrayList<HashMap<String, Object>>();
		myAdapter = new ScoreQueryAdapter(this, R.layout.item, data,mHead);
		RelativeLayout.LayoutParams pas = new RelativeLayout.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		pas.addRule(RelativeLayout.BELOW, mHead.getId());
		mListView1.setLayoutParams(pas);
		mListView1.setAdapter(myAdapter);

		dialog = new ProgressBackDialog(this).getProgressDialog("请稍候...",
				"玩命加载中...");
		dialog.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				// TODO Auto-generated method stub
				handler.setStop(true);
			}
		});
		dialog.show();

		catoryView.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				matchCategory = (String) ((TextView)arg1).getText();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});

		thread = new HandlerThread("post" + i);
		thread.start();
		i++;

		mBackgroundRunnable = new Runnable() { // 初始化数据
			@Override
			public void run() {
				int index = i;
				if (mark == 0) {
					catoryView_DB(index);
					create_scoreRank_data(index);
				} else if (mark == 1) {
					create_scoreRank_data(index);
				}
			}

		};
		handler = new MyHandler(thread.getLooper());
		handlers.add(handler);
		handler.post(mBackgroundRunnable);

		uiHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				switch (msg.what) {
				case 0: // 更新项目
					   catoryView.setAdapter(adapter);
					//	catoryView.setThreshold(1);
						int index = catoryData.indexOf(matchCategory);
						if (index != -1) {
							catoryView.setSelection(index);
						}
					break;
				case 1:
					RelativeLayout.LayoutParams pas = new RelativeLayout.LayoutParams(
							ViewGroup.LayoutParams.WRAP_CONTENT,
							ViewGroup.LayoutParams.WRAP_CONTENT);
					pas.addRule(RelativeLayout.BELOW, mHead.getId());
					mListView1.setLayoutParams(pas);
					mListView1.setAdapter(myAdapter);
				}
			}

		};

		query.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				mark = 1;
				dialog = new ProgressBackDialog(LalaScoreOrderWeb.this)
						.getProgressDialog("请稍候...", "努力加载中...");
				dialog.setOnCancelListener(new OnCancelListener() {
					@Override
					public void onCancel(DialogInterface dialog) {
						// TODO Auto-generated method stub
						handler.setStop(true);
					}
				});
				dialog.show();
				handler.removeCallbacks(mBackgroundRunnable);

				thread = new HandlerThread("post" + i);
				thread.start();
				i++;
				mBackgroundRunnable = new Runnable() {
					@Override
					public void run() {
						int index = i;
						create_scoreRank_data(index);
					}
				};
				handler = new MyHandler(thread.getLooper());
				handlers.add(handler);
				handler.post(mBackgroundRunnable);
			}

		});
	}

	// 获取赛事项目
	@SuppressWarnings("unchecked")
	public void catoryView_DB(int index) {
		LalaScoreDAO dao = new LalaScoreDAO(
				LalaScoreOrderWeb.this.getFilesDir());
		if (dao.checkConfig()) {
			HashMap<String, Object> result = dao.getCatoryData(matchName,
					matchType);
			if (result.get("message").equals("success")) {
				catoryData = (List<String>) result.get("data");
				handlers.get(index).sendEmptyMessage(2);
			} else {
				error = (String) result.get("message");
				handlers.get(index).sendEmptyMessage(1);
			}
		} else {
			handlers.get(index).sendEmptyMessage(0);
		}
	}

	// 获取排名根据赛事项目
	@SuppressWarnings("unchecked")
	public void create_scoreRank_data(int index) {
		LalaScoreDAO dao = new LalaScoreDAO(
				LalaScoreOrderWeb.this.getFilesDir());
		if (dao.checkConfig()) {
			HashMap<String, Object> result = dao.getScoreRankData(matchName,
					matchCategory, matchType);
			if (result.get("message").equals("success")) {
				data = (List<HashMap<String, Object>>) result.get("data");
				handlers.get(index).sendEmptyMessage(3);
			} else {
				error = (String) result.get("message");
				handlers.get(index).sendEmptyMessage(1);
			}
		} else {
			handlers.get(index).sendEmptyMessage(0);
		}
	}

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

			// System.out.println("handler：" + Thread.currentThread().getId());

			if (isStop)
				return;
			switch (msg.what) {
			case 0: {
				dialog.dismiss();
				Toast.makeText(LalaScoreOrderWeb.this, "请检查网络是否畅通！",
						Toast.LENGTH_LONG).show();
				break;
			}
			case 1: {
				dialog.dismiss();
				Toast.makeText(LalaScoreOrderWeb.this, error, Toast.LENGTH_LONG)
						.show();
				break;
			}
			case 2: {
				dialog.dismiss();
				adapter = new ArrayAdapter(LalaScoreOrderWeb.this,
						android.R.layout.simple_dropdown_item_1line, catoryData);
				uiHandler.sendEmptyMessage(0);
				break;
			}
			case 3: {
				dialog.dismiss();
				myAdapter = new ScoreQueryAdapter(LalaScoreOrderWeb.this,
							R.layout.item, data, mHead);
				uiHandler.sendEmptyMessage(1);
			}
			}
		}
	}

	/**
	 * 菜单、返回键响应
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	class ListViewAndHeadViewTouchLinstener implements View.OnTouchListener {

		@Override
		public boolean onTouch(View arg0, MotionEvent arg1) {
			// 当在列头 和 listView控件上touch时，将这个touch的事件分发给 ScrollView
			HorizontalScrollView headSrcrollView = (HorizontalScrollView) mHead
					.findViewById(R.id.horizontalScrollView1);
			headSrcrollView.onTouchEvent(arg1);
			return false;
		}
	}
}
