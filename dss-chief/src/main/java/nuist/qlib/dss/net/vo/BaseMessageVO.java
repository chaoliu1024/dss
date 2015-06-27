package nuist.qlib.dss.net.vo;

import java.io.Serializable;
import java.util.List;

import lombok.Data;
import nuist.qlib.dss.constant.MessageType;
import nuist.qlib.dss.constant.RoleType;

@Data
public abstract class BaseMessageVO implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 信息发送者
	 */
	private RoleType roleType;

	/**
	 * 源ip
	 */
	private String originalIp;

	/**
	 * 目的ip
	 */
	private List<String> targetIps;

	public abstract MessageType messageType();
}
