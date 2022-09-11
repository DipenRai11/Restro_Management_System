package com.restro.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class RestroUtils {
private RestroUtils() {
	
}
public static ResponseEntity<String>getResponseEntity(String responseMessage, HttpStatus httpStatus){
	return new ResponseEntity<String>( "{\"message\":\""+responseMessage+"\"}", httpStatus);
}
}
