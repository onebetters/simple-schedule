package com.qianmi.b2b.scheduling.core.supports.json;

/**
 * <p>Date: 2022-03-25 10:31.</p>
 *
 * @author Administrator
 * @version 0.1.0
 */
public class JsonException extends RuntimeException {
    private static final long serialVersionUID = -5608766636140808310L;

    public JsonException(String message) {
        super(message);
    }

    public JsonException(String message, Throwable cause) {
        super(message, cause);
    }

    public JsonException(Throwable cause) {
        super(cause);
    }
}
