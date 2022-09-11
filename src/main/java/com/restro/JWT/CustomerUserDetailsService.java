package com.restro.JWT;

import java.util.ArrayList;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.restro.Dao.UserDao;
@Service

public class CustomerUserDetailsService implements UserDetailsService {
	@Autowired
	UserDao userDao; 
	
	private com.restro.POJO.User userDetail;
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
	userDetail = userDao.findByEmailId(username);
	if(!Objects.isNull(userDetail)) {
		return new User(userDetail.getEmail(),userDetail.getPassword(),new ArrayList<>());
	}else {
		throw new UsernameNotFoundException("user not found");
	}

	
}
	public com.restro.POJO.User getUserDetail() {
		// TODO Auto-generated method stub
		return userDetail;
	}}
