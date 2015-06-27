/*
 * Copyright (c) 2015, NUIST - 120Lib. All rights reserved.
 */

package nuist.qlib.dss.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CalcScore {

	private static double artFinalScore = 0.0;
	private static double execFinalScore = 0.0;
	private static double impFinalScore = 0.0;

	public HashMap<String, Double> getHashJudgeScore(
			HashMap<String, String> scores) {

		HashMap<String, Double> eachFinalScore = new HashMap<String, Double>();

		artFinalScore = getArtFinalScore(scores.get("art1"),
				scores.get("art2"), scores.get("art3"), scores.get("art4"));

		execFinalScore = getExecFinalScore(scores.get("exec1"),
				scores.get("exec2"), scores.get("exec3"), scores.get("exec4"));

		impFinalScore = getImpFinalScore(scores.get("imp1"), scores.get("imp2"));

		double totalScore = calcTotalScore(artFinalScore, execFinalScore,
				impFinalScore, scores.get("dedu"));

		eachFinalScore.put("artFinal", artFinalScore);
		eachFinalScore.put("execFinal", execFinalScore);
		eachFinalScore.put("impFinal", impFinalScore);
		eachFinalScore.put("totalScore", totalScore);

		return eachFinalScore;
	}

	private Double getArtFinalScore(String art1, String art2, String art3,
			String art4) {

		return calcAvgScoreFore2Two(art1, art2, art3, art4);
	}

	private Double getExecFinalScore(String exec1, String exec2, String exec3,
			String exec4) {
		return calcAvgScoreFore2Two(exec1, exec2, exec3, exec4);
	}

	private Double getImpFinalScore(String imp1, String imp2) {
		double temp = Arith.div(
				Arith.add(Double.parseDouble(imp1), Double.parseDouble(imp2)),
				2.0f);
		return Arith.round(temp, 2);
	}

	/**
	 * calculate total score
	 * 
	 * @param artFinalScore
	 * @param execFinalScore
	 * @param impFinalScore
	 * @param deduFinalscore
	 * 
	 * @since DSS 1.0
	 */
	public Double calcTotalScore(double artFinalScore, double execFinalScore,
			double impFinalScore, String deduFinalscore) {

		Double deductionScore = 0.0;

		if (!deduFinalscore.equals("") && deduFinalscore != null) {
			deductionScore = Double.parseDouble(deduFinalscore);
		}

		double totalScore = artFinalScore + execFinalScore + impFinalScore
				- deductionScore;
		// 保留两位小数
		return Arith.round(totalScore, 2);
	}

	/**
	 * 计算4个分数的总成绩
	 * 
	 * @param score01
	 * @param score02
	 * @param score03
	 * @param score04
	 * 
	 * @since DSS 1.0
	 */
	public Double calcAvgScoreFore2Two(String score01, String score02,
			String score03, String score04) {
		List<Double> scores = new ArrayList<Double>();

		if (scores.size() != 0)
			scores.removeAll(scores);

		scores.add(Double.parseDouble(score01));
		scores.add(Double.parseDouble(score02));
		scores.add(Double.parseDouble(score03));
		scores.add(Double.parseDouble(score04));

		double maxScore = maxScore(scores);
		double minScore = minScore(scores);

		double totalScore = Arith.div(getMidScoreSum(scores), 2.0f);
		String strScore = Arith.round(totalScore, 2) + "";

		return Arith.round(totalScore, 2);
	}

	/**
	 * 得到最低分
	 * 
	 * @param scores
	 * @return 最低分
	 * @since dss 1.0
	 */
	private double maxScore(List<Double> scores) {
		int max = 0;
		for (int i = 1; i < scores.size(); i++) {
			if (scores.get(max) < scores.get(i)) {
				max = i;
			}
		}
		return scores.get(max);
	}

	/**
	 * 得到最高分
	 * 
	 * @param scores
	 * @return 最高分
	 * @since dss 1.0
	 */
	private double minScore(List<Double> scores) {
		int min = 0;
		for (int i = 1; i < scores.size(); i++) {
			if (scores.get(min) > scores.get(i)) {
				min = i;
			}
		}
		return scores.get(min);
	}

	/***
	 * 去除最高分与最低分并算剩下分的和
	 * 
	 * @since DSS 1.0
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
			result = Arith.add(result, scores.get(i));
		}
		return result;
	}

	/**
	 * 获取分数的误差
	 * 
	 * @since DSS 1.0
	 */
	public List<Double> calcDeviations(String artScore01, String artScore02,
			String artScore03, String artScore04, String execScore01,
			String execScore02, String execScore03, String execScore04,
			String impScore01, String impScore02) {

		List<Double> results = new ArrayList<Double>();

		// 艺术分误差
		results.add(Math.abs(Double.parseDouble(artScore01) / artFinalScore - 1));
		results.add(Math.abs(Double.parseDouble(artScore02) / artFinalScore - 1));
		results.add(Math.abs(Double.parseDouble(artScore03) / artFinalScore - 1));
		results.add(Math.abs(Double.parseDouble(artScore04) / artFinalScore - 1));
		// 完成分误差
		results.add(Math.abs(Double.parseDouble(execScore01) / execFinalScore
				- 1));
		results.add(Math.abs(Double.parseDouble(execScore02) / execFinalScore
				- 1));
		results.add(Math.abs(Double.parseDouble(execScore03) / execFinalScore
				- 1));
		results.add(Math.abs(Double.parseDouble(execScore04) / execFinalScore
				- 1));
		// 舞步分误差
		results.add(Math.abs(Double.parseDouble(impScore01) / impFinalScore - 1));
		results.add(Math.abs(Double.parseDouble(impScore02) / impFinalScore - 1));
		return results;
	}
}