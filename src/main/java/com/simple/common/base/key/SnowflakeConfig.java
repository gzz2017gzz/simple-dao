package com.simple.common.base.key;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @author 高振中
 * @summary 【雪花主键生成器】
 * @date 2024-05-10 21:45:31
 **/
@Configuration
public class SnowflakeConfig {
    @Value("${spring.workerId}")
    private int workerId;/* 服务ID,集群部署时每个服务配成不同值 */
    @Value("${spring.dataCenterId}")
    private int dataCenterId;/* 数据中心ID */

    @PostConstruct
    public void init() {
        SnowflakeId.setDataCenterId(dataCenterId);
        SnowflakeId.setWorkerId(workerId);
    }
}
