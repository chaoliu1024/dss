package nuist.qlib.dss.net.vo;

import nuist.qlib.dss.constant.MessageType;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class IPMessageVO extends BaseMessageVO {

	public IPMessageVO() {
		this.setMessageType(MessageType.IP);
	}
}
