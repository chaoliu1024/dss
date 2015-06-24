/*
 * 文件名：QueryScore.java
 * 版权：Copyright 2014 Artisan WangFang
 * 描述：成绩查询，包括查询赛事
 */

package nuist.lib.dss.scoreManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import nuist.lib.dss.dao.QueryScoreDao;
import nuist.lib.dss.util.ExcelManager;

public class QueryScore {

	private QueryScoreDao dao;
	private ExcelManager excelManager;

	public QueryScore() {
		dao = new QueryScoreDao();
		excelManager = new ExcelManager();
	}

	/***
	 * 获取所有已经有成绩的赛事名称
	 * 
	 * @return
	 */
	public String[] getAllMatchNames() {
		List<HashMap<String, Object>> data = dao.getAllMatchNames();
		String[] results = new String[data.size()];
		for (int i = 0; i < data.size(); i++) {
			results[i] = data.get(i).get("matchName").toString();
		}
		return results;
	}

	/**
	 * 在首界面点击排名按钮，若此时界面中没有任何参赛队伍的id，那么根据赛事名称以及赛事类型选择id号最小的队伍
	 * 
	 * @param matchType 赛事类型
	 * @param matchName 赛事名称 * @param matchNum 场次
	 * @return -1 表示赛事该类型没有分数
	 * @return
	 */
	public int getTeamMinId(int matchType, String matchName) {
		List<HashMap<String, Object>> data = dao.getTeamMinId(matchType,
				matchName);
		if (data.size() == 0) {
			return -1;
		} else {
			return Integer.valueOf(data.get(0).get("id").toString());
		}
	}

	/**
	 * 在首界面点击排名按钮，若此时界面中没有任何参赛队伍的id，那么根据赛事名称以及赛事类型选择id号最小的队伍
	 * 
	 * @param matchType 赛事类型
	 * @param matchName 赛事名称
	 * @param matchNum  场次
	 * @param matchOrder 队伍在场次中的序号
	 * @return
	 */
	public int getTeamMinId(int matchType, String matchName, int matchNum, int matchOrder) {
		List<HashMap<String, Object>> data = dao.getTeamMinId(matchType, matchName, matchNum, matchOrder);
		if (data.size() == 0) {
			return -1;
		} else {
			return Integer.valueOf(data.get(0).get("id").toString());
		}
	}

	/** 参数为参赛者的id号,该函数根据参赛者的id号、比赛类型(如预赛或决赛)显示参赛者所属项目的排名情况 ,按照总分排名 */
	public List<HashMap<String, Object>> getRank(int id, int matchType) {
		List<HashMap<String, Object>> data = dao.getRank(id, matchType);
		int i = 1;
		HashMap one=null;
		String score="";
		if(data.size()!=0){
			score=data.get(0).get("total").toString();
			one=data.get(0);
			one.put("rank", i);
		}		
		for (int j=1;j<data.size();j++) {
			one=data.get(j);
			if(one.get("total").toString().equals(score)){
				one.put("rank", i);
			}else{
				i++;
				score=one.get("total").toString();
				one.put("rank", i);
			}			
		}
		return data;
	}

	/** 根据站点、比赛项目、比赛类型获取该项目的比赛排名成绩 */
	public List<HashMap<String, Object>> getRank(String matchName,
			String category, int matchType) {
		List<HashMap<String, Object>> data = dao.getRank(matchName, category,
				matchType);
		int i = 1;
		HashMap one=null;
		String score="";
		if(data.size()!=0){
			score=data.get(0).get("total").toString();
			one=data.get(0);
			one.put("rank", i);
		}		
		for (int j=1;j<data.size();j++) {
			one=data.get(j);
			if(one.get("total").toString().equals(score)){
				one.put("rank", i);
			}else{
				i++;
				score=one.get("total").toString();
				one.put("rank", i);
			}			
		}
		return data;
	}
	
	   /***
	    * 获取要打印的成绩排名(包括队员名称)
	    */
	   public List<HashMap<String,Object>> getPrintRank(String matchName,String category,int matchType){
		   List<HashMap<String,Object>> data=dao.getPrintRank(matchName, category, matchType);
		   int i = 1;
			HashMap one=null;
			String score="";
			if(data.size()!=0){
				score=data.get(0).get("total").toString();
				one=data.get(0);
				one.put("rank", i);
			}		
			for (int j=1;j<data.size();j++) {
				one=data.get(j);
				if(one.get("total").toString().equals(score)){
					one.put("rank", i);
				}else{
					i++;
					score=one.get("total").toString();
					one.put("rank", i);
				}			
			}
		     return data;
	   }

	/**
	 * 根据id号返回它所属的项目
	 * 
	 * @param id
	 * @return
	 */
	public String getCategory(int id) {
		return dao.getCategory(id);
	}

	/**
	 * 获得比赛的项目类型
	 * 
	 * @param matchType 预赛或决赛
	 */
	public String[] getCategories(int matchType, String matchName) {
		List<HashMap<String, Object>> data = dao.getCategories(matchType, matchName);
		String[] result = new String[data.size()];
		for (int i = 0; i < data.size(); i++) {
			result[i] = data.get(i).get("category").toString();
		}
		return result;
	}

	/***
	 * 导出项目成绩
	 * 
	 * @param data
	 * @param fileName
	 * @param matchName
	 * @param category
	 * @param matchType
	 * @param matchKind
	 * @return
	 */
	public boolean exportScoreExcel(List<HashMap<String, Object>> data, String fileName, String matchName, String category, int matchType) {
		return excelManager.ExportScoreExcel(data, fileName, matchName, category, matchType);
	}

	/***
	 * 根据已有成绩赛事名称返回该下面的赛事模式
	 * 
	 * @param matchName
	 * @return
	 */
	public List<Integer> getMatchKindByMatchName(String matchName) {
		List<HashMap<String, Object>> data = dao.getMatchKindByMatchName(matchName);
		List<Integer> result = new ArrayList<Integer>();
		for (int i = 0; i < data.size(); i++) {
			result.add(Boolean.valueOf(data.get(i).get("matchKind").toString()) == false ? 0 : 1);
		}
		return result;
	}

	/***
	 * 判断是否成功的连接了数据库
	 * 
	 * @return
	 */
	public boolean isCollected() {
		return dao.isCollected();
	}

	public void close() {
		if (dao.isCollected()) {
			dao.close();
		}
	}
}
