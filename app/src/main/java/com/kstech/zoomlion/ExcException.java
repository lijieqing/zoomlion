package com.kstech.zoomlion;

/**
 * Created by lijie on 2017/7/6.
 * 使用自定义的异常，即 在可能会出现异常的地方 抛出，然后捕获
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
