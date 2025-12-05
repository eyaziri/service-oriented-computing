package com.smarttourism.attractions.exception;

public class ValidationException extends RuntimeException {
    
    private final ErrorCode errorCode;
    private final String field;
    
    public ValidationException(ErrorCode errorCode, String field, String message) {
        super(message);
        this.errorCode = errorCode;
        this.field = field;
    }
    
    public ValidationException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.field = null;
    }
    
    public ErrorCode getErrorCode() {
        return errorCode;
    }
    
    public String getField() {
        return field;
    }
}