/*
 * Copyright (c) 2015, NUIST - 120Lib. All rights reserved.
 */

package nuist.qlib.dss.constant;

import lombok.Getter;
import lombok.Setter;

public enum MessageType {

	IP("IP信息", "IP"),

	MATCHINFO("比赛信息", "matchInfo"),

	SCORE("单项得分", "score"),

	ALLSOCRE("整体得分", "allScore"),

	COMMAND("调分指令", "command");

	MessageType(String name, String keyWord) {
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
