package com.phs.application.model.response;

public class ResponseOK {
    private String resultCode;
    private String status;
    private String message;

    public ResponseOK(String resultCode, String status, String message) {
        this.resultCode = resultCode;
        this.status = status;
        this.message = message;
    }

// Getters and setters


    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
