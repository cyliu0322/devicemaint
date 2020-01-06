package com.maint.common.exception;

/**
 * 自定义统一异常处理
 * @author aisino
 *
 */
public class ResultException extends RuntimeException {

    private static final long serialVersionUID = 1882153240006350935L;
    
    public ResultException(String message) {
    	super(message);
    }
}
