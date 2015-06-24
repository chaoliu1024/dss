/*
 * 文件名:MatchTypesAdapter.java
 * 版权：Copyright 2014 WangFang
 * 描述：裁判长界面
 */
package com.artisan.dance.adapter;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.artisan.dance.activity.R;

public class MatchTypesAdapter extends BaseAdapter {

	private Context context;
	private List<HashMap<String, Object>> list;

	public MatchTypesAdapter(Context context,
			List<HashMap<String, Object>> list) {
		this.context = context;
		this.list = list;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return list.get(arg0);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.match_type_item, parent, false);
		}
		((TextView) convertView.findViewById(R.id.matchType)).setText(list
				.get(position).get("name").toString());
		return convertView;
	}

}
