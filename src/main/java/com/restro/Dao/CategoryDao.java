package com.restro.Dao;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.restro.POJO.Category;

public interface CategoryDao extends JpaRepository<Category,Integer>{

	
	List<Category>getAllCategory();
}
