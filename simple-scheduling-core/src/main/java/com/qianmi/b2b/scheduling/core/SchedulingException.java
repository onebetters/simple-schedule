package com.qianmi.b2b.scheduling.core;

/**
 * <p>Date: 2022-04-08 16:02.</p>
 *
 * @author Administrator
 * @version 0.1.0
 */
public class SchedulingException extends RuntimeException {
    private static final long serialVersionUID = 5771323565511014311L;

    public SchedulingException(String message) {
        super(message);
    }

    public SchedulingException(String message, Throwable cause) {
        super(message, cause);
    }

    public SchedulingException(Throwable cause) {
        super(cause);
    }
}
