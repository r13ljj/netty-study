package com.jonex.netty.test.production.exception;

/**
 * Created by Guest on 2017/9/12.
 */
public class ConnectFailedException extends RuntimeException{

    public ConnectFailedException() {
        super();
    }

    public ConnectFailedException(String message) {
        super(message);
    }

    public ConnectFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConnectFailedException(Throwable cause) {
        super(cause);
    }

}
