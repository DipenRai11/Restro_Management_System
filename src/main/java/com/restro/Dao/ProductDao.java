package com.restro.Dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.restro.POJO.Product;

public interface ProductDao extends JpaRepository<Product, Integer> {

}
