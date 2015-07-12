package nuist.qlib.dss.net.vo;

import java.util.List;

import lombok.Data;
import nuist.qlib.dss.constant.MessageType;
import nuist.qlib.dss.constant.RoleType;

@Data
public abstract class BaseMessageVO {

	/**
	 * 信息类型
	 */
	private MessageType messageType;

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
	private String targetIp;

}
