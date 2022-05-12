package com.qianmi.b2b.scheduling.configuration.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qianmi.b2b.scheduling.core.supports.json.ObjectMapperManager;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

/**
 * <p>Date: 2022-03-25 10:42.</p>
 *
 * @author Administrator
 * @version 0.1.0
 */
@RequiredArgsConstructor
@Component
public class JsonConfiguration implements InitializingBean {

    private final ObjectMapper objectMapper;

    @Override
    public void afterPropertiesSet() {
        ObjectMapperManager.resetObjectMapper(objectMapper);
    }
}
