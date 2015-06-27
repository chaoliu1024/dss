package nuist.qlib.dss.net.vo;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import nuist.qlib.dss.constant.MessageType;

@Data
@EqualsAndHashCode(callSuper = false)
public class AllScoreMessageVO extends BaseMessageVO implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 艺术得分01
	 */
	private float artScore01;

	/**
	 * 艺术得分02
	 */
	private float artScore02;

	/**
	 * 艺术得分03
	 */
	private float artScore03;

	/**
	 * 艺术得分04
	 */
	private float artScore04;

	/**
	 * 完成得分01
	 */
	private float execScore01;

	/**
	 * 完成得分02
	 */
	private float execScore02;

	/**
	 * 完成得分03
	 */
	private float execScore03;

	/**
	 * 完成得分04
	 */
	private float execScore04;

	/**
	 * 舞步得分01
	 */
	private float impScore01;

	/**
	 * 舞步得分02
	 */
	private float impScore02;

	@Override
	public MessageType messageType() {
		return MessageType.SCORE;
	}

	//

}
