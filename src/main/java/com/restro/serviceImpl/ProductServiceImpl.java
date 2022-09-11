package com.restro.serviceImpl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.restro.Dao.ProductDao;
import com.restro.JWT.JwtFilter;
import com.restro.POJO.Category;
import com.restro.POJO.Product;
import com.restro.constent.RestroConstents;
import com.restro.service.ProductService;
import com.restro.utils.RestroUtils;

@Service
public class ProductServiceImpl implements ProductService {

	@Autowired
	ProductDao productDao;

	@Autowired
	JwtFilter jwtFilter;

	@Override
	public ResponseEntity<String> addNewProduct(Map<String, String> requestMap) {
		try {
			if (jwtFilter.isAdmin()) {
				if (validateProductMap(requestMap, false)) {
					productDao.save(getProductFromMap(requestMap, false));
				}
				return RestroUtils.getResponseEntity(RestroConstents.INVALID_DATA, HttpStatus.BAD_REQUEST);
			} else
				return RestroUtils.getResponseEntity(RestroConstents.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return RestroUtils.getResponseEntity(RestroConstents.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	private boolean validateProductMap(Map<String, String> requestMap, boolean validateId) {

		if (requestMap.containsKey("name")) {
			if (requestMap.containsKey("id") && validateId) {
				return true;
			}
		}
		return false;
	}

	private Product getProductFromMap(Map<String, String> requestMap, boolean isAdd) {

		Category category = new Category();
		category.setId(Integer.parseInt(requestMap.get("categoryId")));
		category.setName(requestMap.get("categoryName"));

		Product product = new Product();
		product.setId(Integer.parseInt(requestMap.get("productId")));

		if (isAdd) {
			product.setId(Integer.parseInt(requestMap.get("id")));
		} else {
			product.setStatus("true");
		}
		product.setCategory(category);
		product.setName(requestMap.get("name"));
		product.setDescription(requestMap.get("description"));
		product.setPrice(Integer.parseInt(requestMap.get("price")));
		product.setCategory(category);
		return product;
	}

}
