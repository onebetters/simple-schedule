package com.qianmi.b2b.scheduling.core.supports;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BooleanSupplier;

/**
 * <p>Date: 2022-04-08 09:50.</p>
 *
 * @author Administrator
 * @version 0.1.0
 */
public class CloseableLock implements AutoCloseable {
    private final BooleanSupplier locker;
    private final AutoCloseableWrapper unLocker;

    private final AtomicBoolean locked = new AtomicBoolean(false);

    public CloseableLock(@NotNull final BooleanSupplier locker, @NotNull final Object unLocker) {
        this.locker = locker;
        this.unLocker = new AutoCloseableWrapper(unLocker);
    }

    public boolean tryLock() {
        locked.set(locker.getAsBoolean());
        return locked.get();
    }

    @Override
    public void close() {
        if (locked.get()) {
            unLocker.close();
        }
    }

    @Slf4j
    public static class AutoCloseableWrapper implements AutoCloseable {
        private final Object locker;

        public AutoCloseableWrapper(Object locker) {
            this.locker = locker;
        }

        @Override
        public void close() {
            if (locker instanceof AutoCloseable) {
                try {
                    ((AutoCloseable) locker).close();
                } catch (Exception e) {
                    log.warn("AutoCloseable close error", e);
                }
            }
        }
    }
}
