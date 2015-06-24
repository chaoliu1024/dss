package com.artisan.dance.service;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class ReceiveInforService extends Service {

	private static final String TAG = "ReceiveInforService";
	public static String units_name;
	public static String category_name;
	private ServerSocket serverSocket;
	public static Boolean Flag = true;

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		try {
			// Log.i(TAG, "serverSocket创建---！");
			this.serverSocket = new ServerSocket(6666);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		new Thread() {
			public void run() {
				receiveInfor();
			};
		}.start();
		return START_NOT_STICKY;
	}

	@Override
	public void onDestroy() {
		Flag = false;
		super.onDestroy();
	}

	private void receiveInfor() {
		try {
			while (Flag) {
				Socket socket = serverSocket.accept(); // 监听
				// 建立输入流
				InputStream is = socket.getInputStream();
				byte[] by = new byte[1024];
				// 将输入流里的字节读到字节数组里，并返回读的字节数
				int length = is.read(by);
				// 将字节数组里的length个字节转换为字符串
				if(length==-1){
					continue;
				}
				String str = new String(by, 0, length, "utf-8");
				System.out.println("str"+str);
				final String message[] = str.split("/");
				System.out.println("mesage[0]:"+message[0]);
				Intent intent = new Intent();
				intent.setAction("android.intent.action.MY_RECEIVER");
				if (message[0].startsWith("art")) { // 接收艺术裁判打分
					intent.putExtra("item", "artScore");
					intent.putExtra("num", message[1]);
					if (message[1].equalsIgnoreCase("01")) {
						intent.putExtra("artScore1", message[2]);
					} else if (message[1].equalsIgnoreCase("02")) {
						intent.putExtra("artScore2", message[2]);
					} else if (message[1].equalsIgnoreCase("03")) {
						intent.putExtra("artScore3", message[2]);
					} else if (message[1].equalsIgnoreCase("04")) {
						intent.putExtra("artScore4", message[2]);
					} 
				} else if(message[0].startsWith("completion")){ // 接收完成裁判打分
					intent.putExtra("item", "completionScore");
					intent.putExtra("num", message[1]);
					if (message[1].equalsIgnoreCase("01")) {
						intent.putExtra("completionScore1", message[2]);
					} else if (message[1].equalsIgnoreCase("02")) {
						intent.putExtra("completionScore2", message[2]);
					} else if (message[1].equalsIgnoreCase("03")) {
						intent.putExtra("completionScore3", message[2]);
					} else if (message[1].equalsIgnoreCase("04")) {
						intent.putExtra("completionScore4", message[2]);
					} 
				} else if(message[0].startsWith("difficult")){ // 接收难度裁判打分
					intent.putExtra("item", "difficult");
					intent.putExtra("difficultScore", message[2]);
					intent.putExtra("difficultSubScore", message[3]);
				} else if(message[0].startsWith("Command")){ // 接收指令
					intent.putExtra("item", "Command");
					intent.putExtra("CommandContent", message[1]);
				} else if (message[0].equalsIgnoreCase("infor1")) { // 接收编辑员发过来的消息
					intent.putExtra("item", "infor1");
					intent.putExtra("team_name", message[1]);
					intent.putExtra("category_name", message[2]);
				} else if (message[0].equalsIgnoreCase("infor2")) { // 接收编辑员发过来的消息
					intent.putExtra("item", "infor2");
					intent.putExtra("team_name", message[1]);					
					intent.putExtra("category_name", message[2]);
					intent.putExtra("match_name", message[3]);
				} else if (message[0].equalsIgnoreCase("infor3")) { // 比赛结束
					intent.putExtra("item", "infor3");
				} else if (message[0].equalsIgnoreCase("all")) {// 接收编辑员发过来的成绩
					intent.putExtra("item", "all");
					intent.putExtra("artScore1", message[1]);
					intent.putExtra("artScore2", message[2]);
					intent.putExtra("artScore3", message[3]);
					intent.putExtra("artScore4", message[4]);
					intent.putExtra("artTotalScore", message[5]);
					intent.putExtra("completionScore1", message[6]);
					intent.putExtra("completionScore2", message[7]);
					intent.putExtra("completionScore3", message[8]);
					intent.putExtra("completionScore4", message[9]);
					intent.putExtra("completionTotalScore", message[10]);
					intent.putExtra("difficultScore", message[11]);
					intent.putExtra("difficultSubScore", message[12]);
					intent.putExtra("deductionSubScore", message[13]);
					intent.putExtra("totalscore", message[14]);
				}
				sendBroadcast(intent);
				socket.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} // 监听端口号
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}
