package nuist.qlib.dss.service;

import java.io.IOException;
import java.net.MulticastSocket;
import java.util.List;

import nuist.qlib.dss.activity.SplashActivity;
import nuist.qlib.dss.net.BroadcastIP;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.view.WindowManager;

public class BroadcastIPService extends Service {

	private static final String TAG = "BroadcastIPService";
	private MulticastSocket sendSocket;
	private Handler ulHandler;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		try {
			sendSocket = new MulticastSocket(9998);
//			new Thread(new BroadcastIP(sendSocket)).start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		ulHandler = new Handler(Looper.getMainLooper()) {
			public void handleMessage(Message msg) {
			 
				switch(msg.what){
				case 0:
					AlertDialog.Builder dialog = new AlertDialog.Builder(
							getApplicationContext());
					dialog.setTitle("警告");
					dialog.setMessage("设备未连接网络，请检查网络后再继续使用！");
					dialog.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									if (BroadcastIP.getI() != 0 && SplashActivity.ReceiveIPIntent != null) {//若在断网且服务请求未销毁，则停止服务
										stopService(SplashActivity.ReceiveIPIntent); // 停止接收IP的服务
									}
									// TODO Auto-generated method stub /
									dialog.dismiss();
								}
							});
					AlertDialog mDialog = dialog.create();
					mDialog.getWindow().setType(
							WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);// 设定为系统级警告，关键
					mDialog.show();
					break;
				case 1:
					AlertDialog.Builder dialog1 = new AlertDialog.Builder(
							getApplicationContext());
					dialog1.setTitle("提示");
					dialog1.setMessage("设备已连接网络，请继续使用！");
					dialog1.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub /
									// 启动接收ip的服务
									if (SplashActivity.ReceiveIPIntent == null) { //若服务请求已销毁，则重新新建请求
										SplashActivity.ReceiveIPIntent = new Intent(BroadcastIPService.this,
												ReceiveIPService.class);										
									}
									if(!isServiceRunning(BroadcastIPService.this, "com.artisan.acrobatics.service.ReceiveIPService")){//若服务不在运行，则重新启动服务
										startService(SplashActivity.ReceiveIPIntent);
									}									
									dialog.dismiss();
								}
							});
					AlertDialog mDialog1 = dialog1.create();
					mDialog1.getWindow()
							.setType(
									WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);// 设定为系统级警告，关键
					mDialog1.show();
				}				
			}

		};
		// 开启广播IP的线程
		new Thread(new BroadcastIP(sendSocket, ulHandler)).start();
		BroadcastIP.Flag = true;
		return START_NOT_STICKY;
	}

	public boolean isServiceRunning(Context mContext,String className) {

        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager)
        mContext.getSystemService(Context.ACTIVITY_SERVICE); 
        List<ActivityManager.RunningServiceInfo> serviceList 
                   = activityManager.getRunningServices(30);

        if (!(serviceList.size()>0)) {
            return false;
        }

        for (int i=0; i<serviceList.size(); i++) {
            if (serviceList.get(i).service.getClassName().equals(className) == true) {
                isRunning = true;
                break;
            }
        }
        return isRunning;
    }
	
	@Override	
	public void onDestroy() {
		BroadcastIP.Flag = false; //关闭线程
		super.onDestroy();
	}
}
