package nuist.qlib.dss.net.vo;

import nuist.qlib.dss.constant.MessageType;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class MatchInfoMessageVO extends BaseMessageVO {

	/**
	 * 参赛单位
	 */
	private String matchUnit;

	/**
	 * 比赛项目
	 */
	private String matchCategory;

	/**
	 * 赛事名称
	 */
	private String matchName;

	public MatchInfoMessageVO() {
		this.setMessageType(MessageType.MATCHINFO);
	}

}
