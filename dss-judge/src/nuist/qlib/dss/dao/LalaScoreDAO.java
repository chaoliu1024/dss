/*
 * 啦啦操比赛成绩DAO操作
 */

package nuist.qlib.dss.dao;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import nuist.qlib.dss.util.ToolUtil;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

public class LalaScoreDAO {

	boolean mark; // 用来表明是否正确取到配置信息
	private File dbConfigParFile; // 配置文件的父路径
	private Object ip;

	/** 构造函数，读取数据库的配置，同时与数据库建立连接 */
	public LalaScoreDAO(File dbConfigParFile) {
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
		if (ip == null || ip.equals("")) {
			return false;
		} else {
			return true;
		}
	}

	/***
	 * 访问网站获取某赛事所有的比赛项目
	 * 
	 * @return
	 */
	public HashMap<String, Object> getCatoryData(String matchName, int matchType) {
		List<String> data = new ArrayList<String>();
		HashMap<String, Object> one = new HashMap<String, Object>();
		String uri = "http://" + ip + ":8080/DanceWeb/getForeCatoryData";
		HttpPost request = new HttpPost(uri);
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("matchName", matchName));
		params.add(new BasicNameValuePair("matchType", String
				.valueOf(matchType)));
		try {
			request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			HttpResponse response = new DefaultHttpClient().execute(request);
			if (response.getStatusLine().getStatusCode() == 404) {
				one.put("message", "没有找到网页");
			} else if (response.getStatusLine().getStatusCode() == 200) {
				String result = EntityUtils.toString(response.getEntity(),
						"UTF-8");
				JSONArray object = new JSONArray(result);
				one.put("message", "success");
				for (int i = 0; i < object.length(); i++) {
					data.add(object.getJSONObject(i).getString("category"));
				}
				one.put("data", data);
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

	/***
	 * 访问网站获取某比赛项目的排名情况
	 * 
	 * @return
	 */
	public HashMap<String, Object> getScoreRankData(String matchName,
			String matchCategory, int matchType) {
		List<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> one = null;
		HashMap<String, Object> oneResult = new HashMap<String, Object>();
		String uri = "http://" + ip
				+ ":8080/DanceWeb/getForeScoreRankData";
		HttpPost request = new HttpPost(uri);
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("matchName", matchName));
		params.add(new BasicNameValuePair("matchType", String
				.valueOf(matchType)));
		params.add(new BasicNameValuePair("matchCategory", matchCategory));
		try {
			request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			HttpResponse response = new DefaultHttpClient().execute(request);
			if (response.getStatusLine().getStatusCode() == 404) {
				oneResult.put("message", "没有找到网页");
			} else if (response.getStatusLine().getStatusCode() == 200) {
				String result = EntityUtils.toString(response.getEntity(),
						"UTF-8");
				JSONArray array = new JSONArray(result);
				JSONObject object = null;
				oneResult.put("message", "success");
				data.clear();
				for (int i = 0; i < array.length(); i++) {
					object = array.getJSONObject(i);
					one = new HashMap<String, Object>();
					one.put("id", object.get("id"));
					one.put("unit", object.get("unit"));
					one.put("score01",
							object.getString("score01").equals("null") ? Double
									.valueOf(0) : new DecimalFormat("#0.00")
									.format(object.getDouble("score01")));
					one.put("score02",
							object.getString("score02").equals("null") ? Double
									.valueOf(0) : new DecimalFormat("#0.00")
									.format(object.getDouble("score02")));
					one.put("score03",
							object.getString("score03") == null
									|| object.getString("score03").equals("null") ? Double
									.valueOf(0) : new DecimalFormat("#0.00")
									.format(object.getDouble("score03")));
					one.put("score04",
							object.getString("score04").equals("null") ? Double
									.valueOf(0) : new DecimalFormat("#0.00")
									.format(object.getDouble("score04")));
					one.put("score05",
							object.getString("score05").equals("null") ? Double
									.valueOf(0) : new DecimalFormat("#0.00")
									.format(object.getDouble("score05")));
					one.put("score06",
							object.getString("score06").equals("null") ? Double
									.valueOf(0) : new DecimalFormat("#0.00")
									.format(object.getDouble("score06")));
					one.put("score07",
							object.getString("score07").equals("null") ? Double
									.valueOf(0) : new DecimalFormat("#0.00")
									.format(object.getDouble("score07")));
					one.put("score08",
							object.getString("score08").equals("null") ? Double
									.valueOf(0) : new DecimalFormat("#0.00")
									.format(object.getDouble("score08")));
					one.put("score09",
							object.getString("score09").equals("null") ? Double
									.valueOf(0) : new DecimalFormat("#0.00")
									.format(object.getDouble("score09")));					
					one.put("sub_score",
							object.getString("sub_score").equals("null") ? Double
									.valueOf(0) : object.getDouble("sub_score"));
					one.put("total", new DecimalFormat("#0.00").format(object.getDouble("total")));
					one.put("category", object.get("category"));
					one.put("rank",object.get("rank"));
					one.put("scoreId", object.get("scoreId"));
					data.add(one);
				}
				oneResult.put("data", data);
			} else {
				oneResult.put("message", "error:"
						+ response.getStatusLine().getStatusCode());
			}
		} catch (Exception e) {
			oneResult.put("message", "异常：" + e.getMessage().toString());
			e.printStackTrace();
		}
		return oneResult;
	}

	/**
	 * 获取该模式下的所有已经比赛过的赛事名称
	 * 
	 * @param matchType
	 * @return
	 */
	public HashMap<String, Object> getLaLaScoreMatchNames(int matchType) {
		List<String> data = new ArrayList<String>();
		List<HashMap<String, Object>> categorysData = new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> one = new HashMap<String, Object>();
		String uri = "http://" + ip
				+ ":8080/DanceWeb/getForeLaLaScoreMatchNames";
		HttpPost request = new HttpPost(uri);
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("matchType", String
				.valueOf(matchType)));
		try {
			request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			HttpResponse response = new DefaultHttpClient().execute(request);
			if (response.getStatusLine().getStatusCode() == 404) {
				one.put("message", "没有找到网页");
			} else if (response.getStatusLine().getStatusCode() == 200) {
				String result = EntityUtils.toString(response.getEntity(),
						"UTF-8");
				JSONObject object = new JSONObject(result);
				JSONArray matchNames = object.getJSONArray("matchNames");
				JSONArray categorys = object.getJSONArray("categorys");
				one.put("message", "success");
				for (int i = 0; i < matchNames.length(); i++) {
					data.add(matchNames.getJSONObject(i).getString("name"));
				}
				one.put("matchNames", data);
				HashMap<String, Object> categorysOne = null;
				if (matchNames.length() != 0) {
					for (int i = 0; i < categorys.length(); i++) {
						categorysOne = new HashMap<String, Object>();
						categorysOne.put("matchName", categorys
								.getJSONObject(i).get("matchName"));
						categorysOne.put("category", categorys.getJSONObject(i)
								.get("category"));
						categorysData.add(categorysOne);
					}
					one.put("categorys", categorysData);
				}
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
}
