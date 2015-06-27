package nuist.qlib.dss.net.vo;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import nuist.qlib.dss.constant.MessageType;

@Data
@EqualsAndHashCode(callSuper = false)
public class IPMessageVO extends BaseMessageVO implements Serializable {

	private static final long serialVersionUID = 1L;

	@Override
	public MessageType messageType() {
		return MessageType.IP;
	}

	//

}
