package com.simple.common.base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.concurrent.atomic.AtomicBoolean;

public interface UserIdProvider {

	Logger log = LoggerFactory.getLogger(UserIdProvider.class);
	AtomicBoolean warned = new AtomicBoolean(false);

	default Long userId() {
		// 仅在应用启动后第一次调用时打一次警告
		if (warned.compareAndSet(false, true)) {
			log.warn("【SimpleDAO警告】当前使用默认UserIdProvider，所有操作的createBy/updateBy将为1000L。生产环境请自定义UserIdProvider实现");
		}
		return 1000L;
	}
}