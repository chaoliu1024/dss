package nuist.qlib.dss.util;

import java.util.ArrayList;
import java.util.List;

public class CalcScore {

	static private double artTotalScore = 0.0;
	static private double execTotalScore = 0.0;
	static private double impTotalScore = 0.0;
	
	private Double deductionScore = 0.0;// 裁判长减分
	
	/**
	 * @Description: calculate total score
	 * @param art_total_score
	 * @param exec_total_score
	 * @param imp_total_score
	 * @param deduction_score
	 * @author LiuChao
	 * @date Sep 6, 2014 10:54:55 AM
	 */
	public String calcTotalScore(String art_total_score, String exec_total_score, String imp_total_score, String deduction_score) {
		
		if(!deduction_score.equals("") && deduction_score != null){
			deductionScore =  Double.parseDouble(deduction_score);
		}
		
		double totalScore = Double.parseDouble(art_total_score) + Double.parseDouble(exec_total_score) + Double.parseDouble(imp_total_score) - deductionScore;
		String strScore = Arith.round(totalScore, 2) + ""; // 保留两位小数
		
		return strScore;
	}
	
	/**
	 * @param score01
	 * @param score02
	 * @param score03
	 * @param score04
	 * @param role art
	 * @author LiuChao
	 * @date Sep 17, 2014 9:18:54 PM
	 * @throws
	 */
	public String calcAvgScoreForeToTwo(String score01, String score02, String score03, String score04, String role){
		
		List<Double> scores = new ArrayList<Double>();
		
		if(scores.size() != 0)
			scores.removeAll(scores);
		
		scores.add(Double.parseDouble(score01));
		scores.add(Double.parseDouble(score02));
		scores.add(Double.parseDouble(score03));
		scores.add(Double.parseDouble(score04));
		
		double totalScore = Arith.div(getMidScoreSum(scores), 2.0f);
//		String strScore = new DecimalFormat("#0.00").format(Arith.round(totalScore, 2));
		String strScore = Arith.round(totalScore, 2) + "";
		if(role.equalsIgnoreCase("art"))
			artTotalScore = Double.parseDouble(strScore);
		
		if(role.equalsIgnoreCase("exec"))
			execTotalScore = Double.parseDouble(strScore);
		
		return strScore;
	}

	/**
	 * 计算总体评价分
	 * @param score01     分数1
	 * @param score02     分数2
	 * @return 总体评价分 
	 * @since  cdss 1.0
	 */
	public String calcAvgTwoScore(String score01, String score02) {
		
		impTotalScore = Arith.div(Arith.add(Double.parseDouble(score01), Double.parseDouble(score02)), 2.0f);
		String strScore = Arith.round(impTotalScore, 2) + "";
		
		return strScore;
	}
	
	/***
	 * 去除最高分与最低分并算剩下分的和
	 * @return
	 */
	public double getMidScoreSum(List<Double> scores) {
		double result = 0;
		int min = 0;
		int max = 0;
		for (int i = 1; i < scores.size(); i++) {
			if (scores.get(min) > scores.get(i)) {
				min = i;
			}
		}
		scores.remove(min);
		for (int i = 1; i < scores.size(); i++) {
			if (scores.get(max) < scores.get(i)) {
				max = i;
			}
		}
		scores.remove(max);

		for (int i = 0; i < scores.size(); i++) {
//			result += scores.get(i);
			result = Arith.add(result, scores.get(i));
		}
		return result;
	}
	
	/**
	 * 获取分数的误差
	 * @return
	 */
	public List<Double> calcDeviations(String artScore01, String artScore02, String artScore03, String artScore04, String execScore01, String execScore02, String execScore03, String execScore04, String impScore01, String impScore02){
		
		List<Double> results = new ArrayList<Double>();
		
		// 艺术分误差
		results.add(Math.abs(Double.parseDouble(artScore01)/artTotalScore-1));
		results.add(Math.abs(Double.parseDouble(artScore02)/artTotalScore-1));
		results.add(Math.abs(Double.parseDouble(artScore03)/artTotalScore-1));
		results.add(Math.abs(Double.parseDouble(artScore04)/artTotalScore-1));
        // 完成分误差
		results.add(Math.abs(Double.parseDouble(execScore01)/execTotalScore-1));
		results.add(Math.abs(Double.parseDouble(execScore02)/execTotalScore-1));
		results.add(Math.abs(Double.parseDouble(execScore03)/execTotalScore-1));
		results.add(Math.abs(Double.parseDouble(execScore04)/execTotalScore-1));
        // 总体评价分误差
        results.add(Math.abs(Double.parseDouble(impScore01)/impTotalScore-1));
        results.add(Math.abs(Double.parseDouble(impScore02)/impTotalScore-1));
        return results;
	}
}