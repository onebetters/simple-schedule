package com.qianmi.b2b.scheduling.core.supports;

import com.qianmi.b2b.commons.utils.id.LongIdGenerator;
import com.qianmi.b2b.commons.utils.id.snowflake.MacAddressNodeIdProvider;
import com.qianmi.b2b.commons.utils.id.snowflake.SnowflakeId;
import org.jetbrains.annotations.NotNull;

/**
 * <p>Date: 2022-03-25 10:20.</p>
 *
 * @author Administrator
 * @version 0.1.0
 */
public final class TaskIds {

    private TaskIds() {
        setGenerator(new SnowflakeId(new MacAddressNodeIdProvider(), 4, 14));
    }

    public static void setGenerator(@NotNull LongIdGenerator idGenerator) {
        InstanceHolder.INSTANCE.put(idGenerator);
    }

    public static String next() {
        return InstanceHolder.INSTANCE.get().getAsBase62();
    }

    private enum InstanceHolder {
        INSTANCE;

        private LongIdGenerator idGenerator;

        void put(LongIdGenerator idGenerator) {
            this.idGenerator = idGenerator;
        }

        LongIdGenerator get() {
            return idGenerator;
        }
    }
}
