/*
 * 文件名:ScoreQueryAdapter.java
 * 版权：Copyright 2014 WangFang
 * 描述：成绩容器
 */
package nuist.qlib.dss.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import nuist.qlib.dss.activity.R;
import nuist.qlib.dss.ui.MyHScrollView;
import nuist.qlib.dss.ui.MyHScrollView.OnScrollChangedListener;
import nuist.qlib.dss.util.ScoreViewHolder;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ScoreQueryAdapter extends BaseAdapter {

	public List<ScoreViewHolder> mHolderList = new ArrayList<ScoreViewHolder>();
	private List<HashMap<String, Object>> data;
	int id_row_layout;
	LayoutInflater mInflater;
	private Context context;
	private RelativeLayout mHead;

	public ScoreQueryAdapter(Context context, int id_row_layout,
			List<HashMap<String, Object>> data,
			RelativeLayout mHead) {
		super();
		this.id_row_layout = id_row_layout;
		mInflater = LayoutInflater.from(context);
		this.data = data;
		this.context = context;
		this.mHead = mHead;
	}

	public List<HashMap<String, Object>> getData() {
		return data;
	}

	public void setData(List<HashMap<String, Object>> data) {
		this.data = data;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return data.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parentView) {
		ScoreViewHolder holder = null;

		HashMap<String, Object> one = data.get(position);
		if (convertView == null) {
			synchronized (context) {
				convertView = mInflater.inflate(id_row_layout, null);
				holder = new ScoreViewHolder();

				MyHScrollView scrollView1 = (MyHScrollView) convertView
						.findViewById(R.id.horizontalScrollView1);

				holder.scrollView = scrollView1;
				holder.txt1 = (TextView) convertView
						.findViewById(R.id.roleTextView);
				holder.txt11 = (TextView) convertView
						.findViewById(R.id.textView11);
				holder.art1 = (TextView) convertView
						.findViewById(R.id.art1);
				holder.art2 = (TextView) convertView
						.findViewById(R.id.art2);
				holder.art3 = (TextView) convertView
						.findViewById(R.id.art3);
				holder.art4 = (TextView) convertView
						.findViewById(R.id.art4);
				holder.artTotal = (TextView) convertView
						.findViewById(R.id.artTotal);
				holder.completion1 = (TextView) convertView
						.findViewById(R.id.completion1);
				holder.completion2 = (TextView) convertView
						.findViewById(R.id.completion2);
				holder.completion3 = (TextView) convertView
						.findViewById(R.id.completion3);
				holder.completion4 = (TextView) convertView
						.findViewById(R.id.completion4);
				holder.completionTotal = (TextView) convertView
						.findViewById(R.id.completionTotal);
				holder.difficult = (TextView) convertView
						.findViewById(R.id.difficult);
				holder.difficult_sub = (TextView) convertView
						.findViewById(R.id.difficult_sub);
				holder.sub_score = (TextView) convertView
						.findViewById(R.id.sub_score);
				holder.total = (TextView) convertView.findViewById(R.id.total);

				MyHScrollView headSrcrollView = (MyHScrollView) mHead
						.findViewById(R.id.horizontalScrollView1);
				headSrcrollView
						.AddOnScrollChangedListener(new OnScrollChangedListenerImp(
								scrollView1));

				convertView.setTag(holder);
				mHolderList.add(holder);
			}
		} else {
			holder = (ScoreViewHolder) convertView.getTag();
		}
		holder.txt1.setText(one.get("rank").toString());
		holder.txt11.setText(one.get("unit").toString());
		holder.art1.setText(one.get("art1").toString());
		holder.art2.setText(one.get("art2").toString());
		holder.art3.setText(one.get("art3").toString());
		holder.art4.setText(one.get("art4").toString());
		holder.artTotal.setText(one.get("artTotal").toString());
		holder.completion1.setText(one.get("completion1").toString());
		holder.completion2.setText(one.get("completion2").toString());
		holder.completion3.setText(one.get("completion3").toString());
		holder.completion4.setText(one.get("completion4").toString());
		holder.completionTotal.setText(one.get("completionTotal").toString());
		holder.difficult.setText(one.get("difficult").toString());
		holder.difficult_sub.setText(one.get("difficult_sub").toString());
		holder.sub_score.setText(one.get("sub_score").toString());
		holder.total.setText(one.get("total").toString());

		return convertView;
	}

	class OnScrollChangedListenerImp implements OnScrollChangedListener {
		MyHScrollView mScrollViewArg;

		public OnScrollChangedListenerImp(MyHScrollView scrollViewar) {
			mScrollViewArg = scrollViewar;
		}

		@Override
		public void onScrollChanged(int l, int t, int oldl, int oldt) {
			mScrollViewArg.smoothScrollTo(l, t);
		}
	};
}
