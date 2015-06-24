/*
 * 文件名:IP.java
 * 版权：Copyright 2014 czf
 * 描述：读取、清空Adress.txt中的IP
 */
package com.artisan.dance.dao;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import android.util.Log;

public class IPDao {
	private String TAG = "ReadIP";
	private File path;
	private FileWriter fw;
	private PrintWriter pw;
	private FileReader fr;
	private BufferedReader br;
	private String messageTemp;
	private List<String> list;

	/** 构造函数，获取根目录 */
	public IPDao(File path) {
		this.path = path;
	}

	/** 读取需要的IP */
	public List<String> getIP(String receiver[]) {
		try {
			fw = new FileWriter(path + "/Adress.txt", true);
			pw = new PrintWriter(fw);
			fr = new FileReader(path + "/Adress.txt");
			br = new BufferedReader(fr);
			list = new ArrayList<String>();

			while ((messageTemp = br.readLine()) != null) { // 遍历配置文件
				if (messageTemp.split("/")[0] == null
						|| messageTemp.split("/")[0].equals("")) {
					continue;
				} else {
					for (int i = 0; i < receiver.length; i++) {
						if (messageTemp.split("/")[0].equalsIgnoreCase(receiver[i])) { // 根据角色名选择所需的IP
							String one = messageTemp.split("/")[1];
							list.add(one);
						}
					}
				}
			}
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		} finally {
			try {
				pw.close();
				fw.close();
				br.close();
				fr.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				Log.e(TAG, e.getMessage());
			}
		}
		return list;
	}

	/** 清空配置文件 (Adress里面IP冲突) */
	public void clearIP() {
		try {
			fw = new FileWriter(path + "/Adress.txt", true);
			fw = new FileWriter(path + "/Adress.txt");
			fw.write("");
			fw.close();
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		} finally {
			try {
				fw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				Log.e(TAG, e.getMessage());
			}
		}
	}
}
