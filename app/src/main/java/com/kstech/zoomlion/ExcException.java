package com.kstech.zoomlion;

/**
 * Created by lijie on 2017/7/6.
 */

public class ExcException extends RuntimeException {

    private Exception exception;
    private String errorMsg;

    /**
     * @param e
     * @param string
     */
    public ExcException(Exception exception, String errorMsg) {
        this.exception = exception;
        this.errorMsg = errorMsg;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}
