package com.example.robotlogin.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;

import java.util.HashMap;
import java.util.Map;

public class DataValidateUtil {
    public static ResponseEntity<Map<String, Object>> validate(Errors errors) {
        if (errors.hasErrors()) {
            Map<String, Object> map = new HashMap<>();
            Map<String, Object> errorsMap = new HashMap<>();
            for (FieldError error : errors.getFieldErrors()) {
                errorsMap.put(error.getField(), error.getDefaultMessage());
            }
            map.put("message", "数据非法");
            map.put("errors", errorsMap);
            return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);

        }
        return null;
    }
}
