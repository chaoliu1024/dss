package com.artisan.dance.adapter;
/*
 * 文件名：RolesAdapter.java
 * 版权：Copyright 2014 Artisan WangFang
 * 描述：角色类型容器
 */
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.artisan.dance.activity.R;


public class RolesAdapter extends BaseAdapter {

	private Context context;
	private List<HashMap<String, Object>> list;

	public RolesAdapter(Context context, List<HashMap<String, Object>> list) {
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
					R.layout.role_item_layout, parent, false);
		}
		((TextView) convertView.findViewById(R.id.role)).setText(list
				.get(position).get("name").toString());
		return convertView;
	}

}
