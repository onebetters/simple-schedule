package com.qianmi.b2b.scheduling.configuration.timer.callback;

import com.qianmi.b2b.scheduling.core.TaskElement;
import org.jetbrains.annotations.NotNull;

/**
 * <p>Date: 2022-03-25 15:18.</p>
 *
 * @author Administrator
 * @version 0.1.0
 */
public class DestinationNoFoundException extends RuntimeException {
    private static final long serialVersionUID = -2272475567813345600L;

    public DestinationNoFoundException(@NotNull TaskElement task) {
        this("找不到destination, task=" + task);
    }

    public DestinationNoFoundException(String message) {
        super(message);
    }

    public DestinationNoFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public DestinationNoFoundException(Throwable cause) {
        super(cause);
    }
}
