/*
 * Copyright (c) 2015, NUIST - 120Lib. All rights reserved.
 */

package nuist.qlib.dss.constant;

import lombok.Getter;
import lombok.Setter;

public enum CommandType {
	UP("上调", "up"),

	DOWN("下调", "down"),

	OK("确定", "ok");

	CommandType(String name, String keyWord) {
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
