package nuist.qlib.dss.net.util;

import java.util.HashMap;
import java.util.Map;

import nuist.qlib.dss.constant.MessageType;
import nuist.qlib.dss.net.service.ReceiveService;
import nuist.qlib.dss.net.service.impl.ReceiveIPServiceImpl;
import nuist.qlib.dss.net.service.impl.ReceiveScoreServiceImpl;
import nuist.qlib.dss.net.vo.BaseMessageVO;

public class ReceiveServiceManager {

	private static Map<MessageType, ReceiveService> serviceMap;

	/**
	 * 根据接收信息类型返回对应的处理类
	 * 
	 * @param msg
	 * @return
	 * @since DSS 1.0
	 */
	public static ReceiveService getReceiveService(BaseMessageVO msg) {
		if (serviceMap == null) {
			synchronized (serviceMap) {
				if (serviceMap == null) {
					serviceMap = new HashMap<MessageType, ReceiveService>();
					initReceiveServiceMap();
				}
			}
		}
		return serviceMap.get(msg.messageType());
	}

	/**
	 * 初始化接收服务map
	 * 
	 * @since DSS 1.0
	 */
	private static void initReceiveServiceMap() {
		serviceMap.put(MessageType.IP, new ReceiveIPServiceImpl());
		serviceMap.put(MessageType.SCORE, new ReceiveScoreServiceImpl());
	}
}
