package nuist.qlib.dss.net.util;

import java.util.HashMap;
import java.util.Map;

import nuist.qlib.dss.constant.MessageType;
import nuist.qlib.dss.net.service.SendService;
import nuist.qlib.dss.net.service.impl.BroadcastIPServiceImpl;
import nuist.qlib.dss.net.service.impl.SendAllScoreServiceImpl;
import nuist.qlib.dss.net.service.impl.SendCommandServiceImpl;
import nuist.qlib.dss.net.service.impl.SendMatchInfoServiceImpl;
import nuist.qlib.dss.net.vo.BaseMessageVO;

public class SendServiceManager {

	private static Map<MessageType, SendService> serviceMap;

	/**
	 * 根据发送信息类型返回对应的处理类
	 * 
	 * @param msg
	 * @return
	 * @since DSS 1.0
	 */
	public static SendService getSendService(BaseMessageVO msg) {
		if (serviceMap == null) {
			synchronized (serviceMap) {
				if (serviceMap == null) {
					serviceMap = new HashMap<MessageType, SendService>();
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
		serviceMap.put(MessageType.IP, new BroadcastIPServiceImpl());
		serviceMap.put(MessageType.MATCHINFO, new SendMatchInfoServiceImpl());
		serviceMap.put(MessageType.ALLSOCRE, new SendAllScoreServiceImpl());
		serviceMap.put(MessageType.COMMAND, new SendCommandServiceImpl());
	}
}
