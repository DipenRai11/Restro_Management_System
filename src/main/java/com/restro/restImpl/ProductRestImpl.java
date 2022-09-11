package com.restro.restImpl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.restro.constent.RestroConstents;
import com.restro.rest.ProductRest;
import com.restro.service.ProductService;
import com.restro.utils.RestroUtils;

@RestController
public class ProductRestImpl implements ProductRest {

@Autowired
ProductService productService;
	
	@Override
	public ResponseEntity<String> addNewProduct(Map<String, String> requestMap) {
		try {
			return productService.addNewProduct(requestMap);  
		} catch (Exception ex) {
ex.printStackTrace();
}return RestroUtils.getResponseEntity(RestroConstents.SOMETHING_WENT_WRONG, HttpStatus.BAD_REQUEST);
	}

}
