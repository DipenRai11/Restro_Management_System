package com.restro.restImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.restro.POJO.Category;
import com.restro.constent.RestroConstents;
import com.restro.rest.CategoryRest;
import com.restro.service.CategoryService;
import com.restro.utils.RestroUtils;

public class CategoryRestImpl implements CategoryRest {

	@Autowired
	CategoryService categoryService;
	
	@Override
	public ResponseEntity<String> addNewCategory(Map<String, String> requestMap) {
try {
	
} catch (Exception ex) {
ex.printStackTrace();}
		return RestroUtils.getResponseEntity(RestroConstents.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@Override
	public ResponseEntity<List<Category>> getAllCategory(String filterValue) {
		try {
			return categoryService.getAllCategory(filterValue);
		} catch (Exception ex) {
ex.printStackTrace();		}
return new ResponseEntity<>(new ArrayList<>(),HttpStatus.INTERNAL_SERVER_ERROR);
}

	@Override
	public ResponseEntity<String> updateCategory(Map<String, String> requestMap) {
		try {
			return categoryService.updateCategory(requestMap);
		} catch (Exception ex) {
ex.printStackTrace();
}return RestroUtils.getResponseEntity(RestroConstents.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
	}

}
