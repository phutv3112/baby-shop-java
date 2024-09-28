package com.phs.application.model.response;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import org.springframework.http.ResponseEntity;

public class ResponseObject <T>  {
    public String code;
    public String message;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public T data;
    public ResponseObject(String code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }
}