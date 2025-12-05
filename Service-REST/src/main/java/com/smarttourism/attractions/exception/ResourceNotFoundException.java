package com.smarttourism.attractions.exception;

public class ResourceNotFoundException extends RuntimeException {
    
    private final ErrorCode errorCode;
    private final String resourceName;
    private final String fieldName;
    private final Object fieldValue;
    
    public ResourceNotFoundException(ErrorCode errorCode, String resourceName, 
                                     String fieldName, Object fieldValue) {
        super(String.format("%s non trouvé avec %s : '%s'", 
              resourceName, fieldName, fieldValue));
        this.errorCode = errorCode;
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }
    
    public ResourceNotFoundException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.resourceName = null;
        this.fieldName = null;
        this.fieldValue = null;
    }
    
    public ErrorCode getErrorCode() {
        return errorCode;
    }
    
    public String getResourceName() {
        return resourceName;
    }
    
    public String getFieldName() {
        return fieldName;
    }
    
    public Object getFieldValue() {
        return fieldValue;
    }
}