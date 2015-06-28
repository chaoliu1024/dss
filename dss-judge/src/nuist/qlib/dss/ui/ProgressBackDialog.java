package nuist.qlib.dss.ui;
/*
 * 文件名：RolesAdapter.java
 * 版权：Copyright 2014 Artisan WangFang
 * 描述：点击back可以取消的进度条
 */
import android.app.ProgressDialog;
import android.content.Context;

public class ProgressBackDialog {
	private Context context;
	
	public ProgressBackDialog(Context context){
		this.context=context;
	}
	public ProgressDialog getProgressDialog(String title, String msg) {
		ProgressDialog progressDialog = new ProgressDialog(context);
		progressDialog.setTitle(title);
		progressDialog.setIndeterminate(true);
		progressDialog.setMessage(msg);
		progressDialog.setCancelable(true);
		return progressDialog;
	}
}
