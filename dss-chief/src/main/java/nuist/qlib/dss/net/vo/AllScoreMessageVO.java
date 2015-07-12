package nuist.qlib.dss.net.vo;

import java.util.Map;

import lombok.Data;
import lombok.EqualsAndHashCode;
import nuist.qlib.dss.constant.RoleType;

@Data
@EqualsAndHashCode(callSuper = false)
public class AllScoreMessageVO extends BaseMessageVO {

	/**
	 * 艺术得分01
	 */
	private float artScore01;

	/**
	 * 艺术得分02
	 */
	private float artScore02;

	/**
	 * 艺术得分03
	 */
	private float artScore03;

	/**
	 * 艺术得分04
	 */
	private float artScore04;

	/**
	 * 艺术总分
	 */
	private float artTotalScore;

	/**
	 * 完成得分01
	 */
	private float execScore01;

	/**
	 * 完成得分02
	 */
	private float execScore02;

	/**
	 * 完成得分03
	 */
	private float execScore03;

	/**
	 * 完成得分04
	 */
	private float execScore04;

	/**
	 * 完成总分
	 */
	private float execTotalScore;

	/**
	 * 舞步得分01
	 */
	private float impScore01;

	/**
	 * 舞步得分02
	 */
	private float impScore02;

	/**
	 * 舞步总分
	 */
	private float impTotalScore;

	/**
	 * 裁判长减分
	 */
	private float subScore;

	/**
	 * 总得分
	 */
	private float totalScore;
	
	/**
	 * 各裁判无效分次数
	 */
	private Map<RoleType, Integer> invaildNum;

}
