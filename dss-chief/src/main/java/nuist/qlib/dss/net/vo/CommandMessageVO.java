package nuist.qlib.dss.net.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import nuist.qlib.dss.constant.CommandType;
import nuist.qlib.dss.constant.MessageType;

@Data
@EqualsAndHashCode(callSuper = false)
public class CommandMessageVO extends BaseMessageVO {

	/**
	 * 调分命令
	 */
	private CommandType commandType;

	public CommandMessageVO() {
		this.setMessageType(MessageType.COMMAND);
	}

}
