package com.qianmi.b2b.scheduling.acceptor.jms;

/**
 * <p>Date: 2022-03-25 14:14.</p>
 *
 * @author Administrator
 * @version 0.1.0
 */
public class JmsTaskException extends RuntimeException{
    private static final long serialVersionUID = 637672351731390093L;

    public JmsTaskException(String message) {
        super(message);
    }

    public JmsTaskException(String message, Throwable cause) {
        super(message, cause);
    }

    public JmsTaskException(Throwable cause) {
        super(cause);
    }
}
