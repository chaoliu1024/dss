package nuist.qlib.dss.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import nuist.qlib.dss.dao.SpringDBAction;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/***
 * 网站的控制逻辑部分
 * 
 * @author fanfan
 *
 */
@Controller
public class FaceController {
	@Resource
	private SpringDBAction springDBAction;

	@RequestMapping(value = { "/hello" }, method = { RequestMethod.GET })
	public String check(HttpServletRequest request, HttpServletResponse response) {
		System.out.print("hello");
		return null;
	}

	/***
	 * 获得所有的角色
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = { "/allRoles" }, method = { RequestMethod.POST })
	@ResponseBody
	public String allRoles(HttpServletRequest request,
			HttpServletResponse response) {
		try {
			request.setCharacterEncoding("utf-8");
			response.setCharacterEncoding("utf-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		String roleDes = request.getParameter("roleDes");
		String sql = "";
		System.out.println(roleDes);
		sql = "select role_name as name,role_value as value from role";

		List<HashMap<String, Object>> data = springDBAction.query(sql,
				new Object[0]);
		try {
			response.getWriter().write(JSONArray.fromObject(data).toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/***
	 * 身份校验
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = { "/roleCheck" }, method = { RequestMethod.POST })
	@ResponseBody
	public String roleCheck(HttpServletRequest request,
			HttpServletResponse response) {
		try {
			request.setCharacterEncoding("utf-8");
			response.setCharacterEncoding("utf-8");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String role = request.getParameter("role");
		String sql = "select login_state as state from role where role_value=?";
		List<HashMap<String, Object>> data = springDBAction.query(sql,
				new Object[] { role });
		String result = "";
		if (data.get(0).get("state").toString().equals("true")) {
			result = "false"; // 该身份已经有人登陆过
		} else {
			if (this.springDBAction.update(
					"update role set login_state=? where role_value=?",
					new Object[] { 1, role }) > 0) {
				result = "true"; // 成功登陆
			} else
				result = "fail"; // 登陆失败
		}
		try {
			System.out.println("登陆结果：" + result);
			response.getWriter().write(result);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/***
	 * 退出登陆
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = { "/loginOut" }, method = { RequestMethod.POST })
	@ResponseBody
	public String loginOut(HttpServletRequest request,
			HttpServletResponse response) {
		try {
			request.setCharacterEncoding("utf-8");
			response.setCharacterEncoding("utf-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		String result;
		String role = request.getParameter("role");
		if (this.springDBAction.update(
				"update role set login_state=? where role_value=?",
				new Object[] { 0, role }) > 0) {
			result = "true"; // 成功退出
		} else
			result = "fail"; // 退出失败
		try {
			System.out.println("退出登陆：" + result + ":" + role);
			response.getWriter().write(result);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	// /***
	// * 获取外国比赛成绩的排名
	// * @param request
	// * @param response
	// * @return
	// */
	// @RequestMapping(value="/getForeScoreRankData",method=RequestMethod.POST)
	// @ResponseBody
	// public String getForeScoreRankData(HttpServletRequest
	// request,HttpServletResponse response){
	// try {
	// request.setCharacterEncoding("utf-8");
	// response.setCharacterEncoding("utf-8");
	// } catch (UnsupportedEncodingException e1) {
	// // TODO Auto-generated catch block
	// e1.printStackTrace();
	// }
	// String matchName=request.getParameter("matchName");
	// int matchType=Integer.valueOf(request.getParameter("matchType"));
	// String matchCategory=request.getParameter("matchCategory");
	// String
	// sql="select b.id as id,b.match_units as unit,b.match_num as matchNum,b.member_name as memberName,a.score01 as score01,a.score02 as score02,a.score03 as score03,a.score04 as score04,a.score05 as score05,a.score06 as score06,a.score07 as score07,a.score08 as score08,a.score09 as score09,a.sub_score as sub_score,a.total as total,a.id as scoreId,b.match_category as category from score as a,match_order as b where a.team_id=b.id and b.match_category=? and b.match_name=? and b.final_preliminary=? "
	// +
	// "order by a.total desc";
	// List<HashMap<String,Object>> data=springDBAction.query(sql, new
	// Object[]{matchCategory,matchName,matchType});
	// int i = 1;
	// HashMap one=null;
	// String score="";
	// if(data.size()!=0){
	// score=data.get(0).get("total").toString();
	// one=data.get(0);
	// one.put("rank", i);
	// }
	// for (int j=1;j<data.size();j++) {
	// one=data.get(j);
	// if(one.get("total").toString().equals(score)){
	// one.put("rank", i);
	// i++;
	// }else{
	// i++;
	// score=one.get("total").toString();
	// one.put("rank", i);
	// }
	// }
	// try {
	// response.getWriter().write(JSONArray.fromObject(data).toString());
	// } catch (IOException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// return null;
	// }
	//
	// /***
	// * 按照外国比赛名称以及赛事模式取出其中所有的项目
	// * @param request
	// * @param response
	// * @return
	// */
	// @RequestMapping(value={"/getForeCatoryData"},
	// method={RequestMethod.POST})
	// @ResponseBody
	// public String getForeCatoryData(HttpServletRequest
	// request,HttpServletResponse response){
	// try {
	// request.setCharacterEncoding("utf-8");
	// response.setCharacterEncoding("utf-8");
	// } catch (UnsupportedEncodingException e1) {
	// // TODO Auto-generated catch block
	// e1.printStackTrace();
	// }
	// String matchName=request.getParameter("matchName");
	// int matchType=Integer.valueOf(request.getParameter("matchType"));
	// List<HashMap<String,Object>> data=
	// springDBAction.query("select distinct match_category as category from match_order where match_name=? and final_preliminary=?",
	// new Object[]{matchName,matchType});
	// try {
	// response.getWriter().write(JSONArray.fromObject(data).toString());
	// } catch (IOException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// return null;
	// }
	//
	// /***
	// * 获得该赛事模式下所有已经比过的赛事名称
	// * @param request
	// * @param response
	// * @return
	// */
	// @RequestMapping(value={"/getForeLaLaScoreMatchNames"},
	// method={RequestMethod.POST})
	// @ResponseBody
	// public String getForeLaLaScoreMatchNames(HttpServletRequest
	// request,HttpServletResponse response){
	// JSONObject one=null;
	// JSONArray array=new JSONArray();
	// try {
	// request.setCharacterEncoding("utf-8");
	// response.setCharacterEncoding("utf-8");
	// } catch (UnsupportedEncodingException e1) {
	// // TODO Auto-generated catch block
	// e1.printStackTrace();
	// }
	// int matchType=Integer.valueOf(request.getParameter("matchType"));
	// String sql=
	// "select distinct b.match_name as name from score a,match_order b where a.team_id=b.id and b.final_preliminary=?";
	//
	// List<HashMap<String,Object>> data=springDBAction.query(sql, new
	// Object[]{matchType});
	// List<HashMap<String,Object>> catorys=null;
	// JSONObject oneMap=null;
	// sql="select distinct b.match_category as category from score a,match_order b where a.team_id=b.id and b.final_preliminary=? and b.match_name=?";
	// if(data.size()!=0){
	// for(int i=0;i<data.size();i++){
	// catorys=this.springDBAction.query(sql, new
	// Object[]{matchType,data.get(i).get("name")});
	// oneMap=new JSONObject();
	// oneMap.put("matchName", data.get(i).get("name"));
	// oneMap.put("category", catorys.get(0).get("category"));
	// array.add(oneMap);
	// }
	// }
	// one=new JSONObject();
	// one.put("matchNames", JSONArray.fromObject(data));
	// one.put("categorys", array);
	// try {
	// response.getWriter().write(one.toString());
	// } catch (IOException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// return null;
	// }
}
