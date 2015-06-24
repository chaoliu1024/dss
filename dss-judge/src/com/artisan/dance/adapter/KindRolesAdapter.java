package com.artisan.dance.adapter;
/*
 * 文件名：KindRolesAdapter.java
 * 版权：Copyright 2014 Artisan WangFang
 * 描述：角色容器
 */
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

import com.artisan.dance.activity.R;
import com.artisan.dance.util.ViewHolder;

public class KindRolesAdapter extends BaseAdapter {

	private Context context;
	private List<HashMap<String, Object>> list;

	public KindRolesAdapter(Context context,
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
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.roles_dialog_item, parent, false);
			holder.txt = (TextView) convertView
					.findViewById(R.id.spinner_txt);
			holder.radio = (RadioButton) convertView
					.findViewById(R.id.spinner_radio);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.txt.setText(list.get(position).get("name").toString());
		return convertView;
	}
}
