/*
 * 文件名：LoginDao.java
 * 版权：Copyright 2014 Artisan WangFang
 * 描述：登陆Dao
 */
package com.artisan.dance.dao;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;

import com.artisan.dance.util.ToolUtil;

public class LoginDao {
	boolean mark; // 用来表明是否正确取到配置信息
	private File dbConfigParFile;   //配置文件的父路径fs
	private Object ip;

	/** 构造函数，读取数据库的配置，同时与数据库建立连接 */
	public LoginDao(File dbConfigParFile) {
		this.dbConfigParFile = dbConfigParFile; 
	}
	
	/***
	 * 判断连接数据库的配置信息是否齐全
	 * 
	 * @return
	 */
	public boolean checkConfig() {
		HashMap<String, Object> config = ToolUtil.getDBConfig(dbConfigParFile);
		ip = config.get("ip");
		if (ip == null|| ip.equals("")) {
			return false;
		} else {
			return true;
		}
	}
	
	/***
	 * 登陆时获取所有的登陆角色
	 * @return
	 */
	public HashMap<String, Object> getRoles(String roleDes) {
		List<HashMap<String,Object>> data = new ArrayList<HashMap<String,Object>>();
		HashMap<String, Object> one = new HashMap<String, Object>();
		HashMap<String,Object> oneJSON=null;
		String uri = "http://"+ip+":8080/DanceWeb/allRoles";
		HttpPost request = new HttpPost(uri);
		List<NameValuePair> param = new ArrayList<NameValuePair>();
		param.add(new BasicNameValuePair("roleDes", roleDes));
		try {
			request.setEntity(new UrlEncodedFormEntity(param, HTTP.UTF_8));
			HttpResponse response = new DefaultHttpClient().execute(request);
			if (response.getStatusLine().getStatusCode() == 404) {
				one.put("message", "没有找到网页");
			} else if (response.getStatusLine().getStatusCode() == 200) {
				String result = EntityUtils.toString(response.getEntity(),
						"UTF-8");
				JSONArray array = new JSONArray(result);				
				if(array.length()!=0){
					one.put("message", "success");
					for(int i=0;i<array.length();i++){
						oneJSON=new HashMap<String,Object>();
						oneJSON.put("name", array.getJSONObject(i).getString("name"));
						oneJSON.put("value", array.getJSONObject(i).getString("value"));
						data.add(oneJSON);
					}
					one.put("data", data);
				}else{
					one.put("message", "noData");
				}
			} else {
				one.put("message", "error:"
						+ response.getStatusLine().getStatusCode());
			}
		} catch (Exception e) {
		    one.put("message", "异常：" + e.getMessage().toString());
		//	one.put("message", "异常：");
			e.printStackTrace();
		}
		return one;
	}
	
	/***
	 * 登录提交
	 * @return
	 */
	public HashMap<String, Object> roleCheck(String role) {
		HashMap<String, Object> one = new HashMap<String, Object>();
		String uri = "http://"+ip+":8080/DanceWeb/roleCheck";
		HttpPost request = new HttpPost(uri);
		List<NameValuePair> param = new ArrayList<NameValuePair>();
		param.add(new BasicNameValuePair("role", role));
		try {
			request.setEntity(new UrlEncodedFormEntity(param, HTTP.UTF_8));
			HttpResponse response = new DefaultHttpClient().execute(request);
			if (response.getStatusLine().getStatusCode() == 404) {
				one.put("message", "没有找到网页");
			} else if (response.getStatusLine().getStatusCode() == 200) {
				String result = EntityUtils.toString(response.getEntity(),
						"UTF-8");
				one.put("message", "success");
				one.put("result", result);
			} else {
				one.put("message", "error:"
						+ response.getStatusLine().getStatusCode());
			}
		} catch (Exception e) {
			one.put("message", "异常：" + e.getMessage().toString());
			e.printStackTrace();
		}
		return one;
	}
	
	public HashMap<String, Object> loginOut(String role) {
		HashMap<String, Object> one = new HashMap<String, Object>();
		String uri = "http://"+ip+":8080/DanceWeb/loginOut";
		HttpPost request = new HttpPost(uri);
		List<NameValuePair> param = new ArrayList<NameValuePair>();
		param.add(new BasicNameValuePair("role", role));
		System.out.println(role);
		try {
			request.setEntity(new UrlEncodedFormEntity(param, HTTP.UTF_8));
			HttpResponse response = new DefaultHttpClient().execute(request);
			if (response.getStatusLine().getStatusCode() == 404) {
				one.put("message", "没有找到网页");
			} else if (response.getStatusLine().getStatusCode() == 200) {
				String result = EntityUtils.toString(response.getEntity(),
						"UTF-8");
				one.put("message", "success");
				one.put("result", result);
			} else {
				one.put("message", "error:"
						+ response.getStatusLine().getStatusCode());
			}
		} catch (Exception e) {
			one.put("message", "异常："+e.getMessage().toString() );
			e.printStackTrace();
		}
		return one;
	}
}
