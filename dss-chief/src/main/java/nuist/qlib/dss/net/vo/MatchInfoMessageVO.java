package nuist.qlib.dss.net.vo;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import nuist.qlib.dss.constant.MessageType;

@Data
@EqualsAndHashCode(callSuper = false)
public class MatchInfoMessageVO extends BaseMessageVO implements Serializable {

	private static final long serialVersionUID = 1L;

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

	@Override
	public MessageType messageType() {
		return MessageType.MATCHINFO;
	}

	//

}
