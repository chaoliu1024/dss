/*
 * Copyright (c) 2015, NUIST - 120Lib. All rights reserved.
 */

package nuist.qlib.dss.constant;

import lombok.Getter;
import lombok.Setter;

public enum RoleType {

	EDITOR("记录员", "Editor"),

	ARTJUDGE01("艺术裁判01", "artJudge01"),

	ARTJUDGE02("艺术裁判02", "artJudge02"),

	ARTJUDGE03("艺术裁判03", "artJudge03"),

	ARTJUDGE04("艺术裁判04", "artJudge04"),

	EXECJUDGE01("完成裁判01", "execJudge01"),

	EXECJUDGE02("完成裁判02", "execJudge02"),

	EXECJUDGE03("完成裁判03", "execJudge03"),

	EXECJUDGE04("完成裁判04", "execJudge04"),

	IMPJUDGE01("舞步裁判01", "impJudge01"),

	IMPJUDGE02("舞步裁判02", "impJudge02");

	RoleType(String name, String keyWord) {
		this.name = name;
		this.keyWord = keyWord;
	}

	@Setter
	@Getter
	private String name;

	@Setter
	@Getter
	private String keyWord;

}
