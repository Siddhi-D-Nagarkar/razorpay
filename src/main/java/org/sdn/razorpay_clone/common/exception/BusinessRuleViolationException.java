package org.sdn.razorpay_clone.common.exception;

public class BusinessRuleViolationException extends RuntimeException{
    String errorCode;

    public BusinessRuleViolationException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}
