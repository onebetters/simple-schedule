package com.qianmi.b2b.scheduling.configuration.id;

import com.qianmi.b2b.commons.utils.id.LongIdGenerator;
import com.qianmi.b2b.commons.utils.id.snowflake.MacAddressNodeIdProvider;
import com.qianmi.b2b.commons.utils.id.snowflake.NodeIdProvider;
import com.qianmi.b2b.commons.utils.id.snowflake.SnowflakeId;
import com.qianmi.b2b.scheduling.core.supports.TaskIds;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <p>Date: 2022-03-22 18:45.</p>
 *
 * @author Administrator
 * @version 0.1.0
 */
@Configuration
public class IdGeneratorConfiguration {

    @Bean
    NodeIdProvider nodeIdProvider() {
        return new MacAddressNodeIdProvider();
    }

    @Bean
    LongIdGenerator longIdGenerator(NodeIdProvider nodeIdProvider) {
        final LongIdGenerator idGenerator = new SnowflakeId(nodeIdProvider, 4, 14);
        TaskIds.setGenerator(idGenerator);
        return idGenerator;
    }
}
