package com.restro.serviceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.google.common.base.Strings;
import com.restro.Dao.UserDao;
import com.restro.JWT.CustomerUserDetailsService;
import com.restro.JWT.JwtFilter;
import com.restro.JWT.JwtUtils;
import com.restro.POJO.User;
import com.restro.constent.RestroConstents;
import com.restro.service.UserService;
import com.restro.utils.EmailUtils;
import com.restro.utils.RestroUtils;
import com.restro.wrapper.UserWrapper;

import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
@Autowired
UserDao userDao;
@Autowired
AuthenticationManager authenticationManager;
@Autowired
CustomerUserDetailsService customerUserDetailsService;

@Autowired
JwtUtils jwtUtils;

@Autowired
JwtFilter jwtFilter;

@Autowired
EmailUtils emailUtils;

	@Override
	public ResponseEntity<String> signup(Map<String, String> requestMap) {
log.info("Inside signUp{}",requestMap);
try {
		if(validateSignUpMap(requestMap)) {
			User user =userDao.findByEmailId(requestMap.get("email"));
			if(Objects.isNull(user)) {
				userDao.save(getUserFromMap(requestMap));
				return RestroUtils.getResponseEntity("Successfully Register",HttpStatus.OK);
			}
			else
			{
				return RestroUtils.getResponseEntity("Email already exits.",HttpStatus.BAD_REQUEST);
			}
			
		}
		else {
			return RestroUtils.getResponseEntity(RestroConstents.INVALID_DATA, HttpStatus.BAD_REQUEST);
		}
}catch(Exception ex) {
	ex.printStackTrace();
}
return RestroUtils.getResponseEntity(RestroConstents.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
	}
	private boolean validateSignUpMap(Map<String,String> requestMap) {
	if	(requestMap.containsKey("email") && requestMap.containsKey("contactNumber")&& requestMap.containsKey("email")&& requestMap.containsKey("password")) { 
		
		return true;
	
	}
	return false;
	}
	private User getUserFromMap(Map<String,String>requestMap) {
		User user=new User();
		user.setName(requestMap.get("name"));
		user.setContactNumber(requestMap.get("contactNumber"));
		user.setEmail(requestMap.get("email"));
		user.setPassword(requestMap.get("password"));
		user.setStatus(requestMap.get("false"));
		user.setRole(requestMap.get("user"));
		return user;
	}
	@Override
	public ResponseEntity<String> login(Map<String, String> requestMap) {
		log.info("Inside loin");
		try {
			Authentication auth= authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(requestMap.get("email"),requestMap.get("password"))
					);
			if(auth.isAuthenticated()) {
				if(customerUserDetailsService.getUserDetail().getStatus().equalsIgnoreCase("true")) {
					return new ResponseEntity<String>("{\"token\":\"" + jwtUtils.generateToken(customerUserDetailsService.getUserDetail().getEmail(),customerUserDetailsService.getUserDetail().getRole())+"\"}",
							HttpStatus.OK);
				}
				else {
					return new ResponseEntity<String>("{\"message\":\""+"wait for admin response."+"\"}",HttpStatus.BAD_REQUEST);
				}
			}
		}catch(Exception ex) {
			log.error("{}",ex);
		}
			return new ResponseEntity<String>("{\"message\":\""+"Request Terminated.."+"\"}",HttpStatus.BAD_REQUEST);
	}
	@Override
	public ResponseEntity<List<UserWrapper>> getAllUser() {
		try {
			if(jwtFilter.isAdmin()) {
				return new ResponseEntity<>(userDao.getAllUser(),HttpStatus.OK);
		}
			else {
				return new ResponseEntity<>(new ArrayList<>(),HttpStatus.UNAUTHORIZED);
			}}
			catch (Exception ex) {
ex.printStackTrace();		}
		return  new ResponseEntity<>(new ArrayList<>(),HttpStatus.INTERNAL_SERVER_ERROR);
	}
	@Override
	public ResponseEntity<String> update(Map<String, String> requestMap) {
		try {
			if(jwtFilter .isAdmin()) {
			Optional<User> optional=userDao.findById(Integer.parseInt(requestMap.get("id")));
			if(!optional.isEmpty()) {
				userDao.updateStatus(requestMap.get("status"),Integer.parseInt(requestMap.get("id")));
				sendMailToAllAdmin(requestMap.get("status"), optional.get().getEmail(),userDao.getAllAdmin());
				return RestroUtils.getResponseEntity("User status updated successfully",HttpStatus.OK);
			}
			else {
				return RestroUtils.getResponseEntity("User id does not exist",HttpStatus.OK);
			}
			}else {
				return  RestroUtils.getResponseEntity(RestroConstents.UNAUTHORIZED_ACCESS,HttpStatus.UNAUTHORIZED);
			}
		} catch (Exception ex) {
ex.printStackTrace();		}
		return RestroUtils.getResponseEntity(RestroConstents.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
	}
	private void sendMailToAllAdmin(String status, String user, List<String> allAdmin) {
allAdmin.remove(jwtFilter.getCurrentUser());
if(status !=null && status.equalsIgnoreCase("true")) {
	emailUtils.sendSimpleMessage(jwtFilter.getCurrentUser(),"Account Approved","USER:-"+user+ "\n is approved by \n Admin:-"
		+jwtFilter.getCurrentUser(),allAdmin);
}else {
	emailUtils.sendSimpleMessage(jwtFilter.getCurrentUser(),"Account Disabled","USER:-"+user+ "\n is disabled by \n Admin:-"
			+jwtFilter.getCurrentUser(),allAdmin);
}
	}
	@Override
	public ResponseEntity<String> checkToken() {
		return RestroUtils.getResponseEntity("true",HttpStatus.OK);
	}
	@Override
	public ResponseEntity<String> changePassword(Map<String, String> requestMap) {
		try {
			User userObj = userDao.findByEmailId(jwtFilter.getCurrentUser());
			if(!userObj.equals(null)) {
				if(userObj.getPassword().equals(requestMap.get("oldPassword"))) {
					userObj.setPassword(requestMap.get("newPassword"));
					userDao.save(userObj);
					return RestroUtils.getResponseEntity("password updated successfully", HttpStatus.OK);
					
				}
				return RestroUtils.getResponseEntity("Incorrect password",HttpStatus.BAD_REQUEST);
			}
			return RestroUtils.getResponseEntity(RestroConstents.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception ex) {
ex.printStackTrace();
}
		return RestroUtils.getResponseEntity(RestroConstents.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
	}
	@Override
	public ResponseEntity<String> forgotPassword(Map<String, String> requestMap) {
		try {
			User user = userDao.findByEmail(requestMap.get("email"));
			if(!Objects.isNull(user)&& !Strings.isNullOrEmpty(user.getEmail())) 
				emailUtils.forgotMail(user.getEmail(),"credential by Restro Management system" , user.getPassword());
				return RestroUtils.getResponseEntity("check your mail for credential.",HttpStatus.OK); 
				
			
		} catch (Exception ex) {
ex.printStackTrace();
}return RestroUtils.getResponseEntity(RestroConstents.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
