package nuist.qlib.dss.net.vo;

import nuist.qlib.dss.constant.MessageType;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class ScoreMessageVO extends BaseMessageVO {

	/**
	 * 单项得分
	 */
	private float score;

	public ScoreMessageVO() {
		this.setMessageType(MessageType.SCORE);
	}

}
