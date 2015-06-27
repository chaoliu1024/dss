package nuist.qlib.dss.net.vo;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import nuist.qlib.dss.constant.MessageType;

@Data
@EqualsAndHashCode(callSuper = false)
public class ScoreMessageVO extends BaseMessageVO implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 单项得分
	 */
	private float score;

	@Override
	public MessageType messageType() {
		return MessageType.SCORE;
	}

	//

}
