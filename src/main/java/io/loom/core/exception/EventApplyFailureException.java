package io.loom.core.exception;

/**
 * Created by mhyeon.lee on 2017. 5. 7..
 */
public class EventApplyFailureException extends RuntimeException {
    public EventApplyFailureException(String message, Throwable cause) {
        super(message, cause);
    }
}
