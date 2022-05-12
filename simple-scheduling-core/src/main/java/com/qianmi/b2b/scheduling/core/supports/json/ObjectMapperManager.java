package com.qianmi.b2b.scheduling.core.supports.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.jetbrains.annotations.NotNull;

/**
 * <p>Date: 2022-03-25 10:33.</p>
 *
 * @author Administrator
 * @version 0.1.0
 */
public final class ObjectMapperManager {

    private ObjectMapperManager() {
        final ObjectMapper defaultObjectMapper = new ObjectMapper().findAndRegisterModules()
                .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true)
                .setSerializationInclusion(JsonInclude.Include.NON_NULL);
        resetObjectMapper(defaultObjectMapper);
    }

    public static void resetObjectMapper(@NotNull ObjectMapper objectMapper) {
        Holder.INSTANCE.set(objectMapper);
    }

    @NotNull
    public static ObjectMapper get() {
        return Holder.INSTANCE.get();
    }

    private enum Holder {

        INSTANCE;

        private ObjectMapper instance;

        private ObjectMapper get() {
            return instance;
        }

        private void set(ObjectMapper instance) {
            this.instance = instance;
        }
    }
}
