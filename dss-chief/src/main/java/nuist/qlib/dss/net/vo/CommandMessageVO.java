package nuist.qlib.dss.net.vo;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import nuist.qlib.dss.constant.CommandType;
import nuist.qlib.dss.constant.MessageType;

@Data
@EqualsAndHashCode(callSuper = false)
public class CommandMessageVO extends BaseMessageVO implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 调分命令
	 */
	private CommandType commandType;

	@Override
	public MessageType messageType() {
		return MessageType.COMMAND;
	}

	//

}
