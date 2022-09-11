package com.restro.serviceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.base.Strings;
import com.restro.Dao.CategoryDao;
import com.restro.JWT.JwtFilter;
import com.restro.POJO.Category;
import com.restro.constent.RestroConstents;
import com.restro.service.CategoryService;
import com.restro.utils.RestroUtils;

@RestController

public class CategoryServiceImpl implements CategoryService {

	@Autowired
	CategoryDao categoryDao;
	
	@Autowired
	JwtFilter jwtFilter;
	
	@Override
	public ResponseEntity<String> addNewCategory(Map<String, String> requestMap) {
	try {
		if(jwtFilter.isAdmin()) {
			if(validateCategoryMap(requestMap,false)) {
				categoryDao.save(getCategoryFromMap(requestMap,false));
				return RestroUtils.getResponseEntity("Category Added Successfully",HttpStatus.OK);
		}
		else {
			return RestroUtils.getResponseEntity(RestroConstents.UNAUTHORIZED_ACCESS,HttpStatus.UNAUTHORIZED);
		}
	}} catch (Exception ex) {
ex.printStackTrace();	}	
		return RestroUtils.getResponseEntity(RestroConstents.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
	}



	private boolean validateCategoryMap(Map<String, String> requestMap, boolean validateId) {
		if(requestMap.containsKey("name")) {
			if(requestMap.containsKey("id") && validateId) {
				return true;
			} else if (!validateId){
				return true;
			}
		}
		return false;
	}
		private Category getCategoryFromMap(Map<String, String>requestMap,Boolean isAdd) {
			Category category= new Category();
			if(isAdd) {
				category.setId(Integer.parseInt(requestMap.get("id")));
			}
			category.setName(requestMap.get("name"));
			return category;
		}



		@Override
		public ResponseEntity<List<Category>> getAllCategory(String filterValue) {
			try {
				if(!Strings.isNullOrEmpty(filterValue)&& filterValue.equalsIgnoreCase("true ")) {
					return new ResponseEntity<List<Category>>(categoryDao.getAllCategory(),HttpStatus.OK);
				}
				return new ResponseEntity<>(categoryDao.findAll(),HttpStatus.OK);
			} catch (Exception ex) {
               ex.printStackTrace();
}return new ResponseEntity<List<Category>>(new ArrayList<>(),HttpStatus.INTERNAL_SERVER_ERROR);
		}



		@Override
		public ResponseEntity<String> updateCategory(Map<String, String> requestMap) {
try {
	if(jwtFilter.isAdmin()) {
	if(validateCategoryMap(requestMap, true))	{
	Optional  optional =	categoryDao.findById(Integer.parseInt(requestMap.get("id")));
	if(!optional.isEmpty()) {
		categoryDao.save(getCategoryFromMap(requestMap,true));
		return RestroUtils.getResponseEntity("Category Updated Successfully",HttpStatus.OK);
	}
	else {
		return RestroUtils.getResponseEntity("Category id does not exist",HttpStatus.OK);
	}
	}
	return RestroUtils.getResponseEntity(RestroConstents.INVALID_DATA,HttpStatus.BAD_REQUEST);
	}
	else
	{
		return RestroUtils.getResponseEntity(RestroConstents.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
	}
} catch (Exception ex) {
ex.printStackTrace();}	
return RestroUtils.getResponseEntity(RestroConstents.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
