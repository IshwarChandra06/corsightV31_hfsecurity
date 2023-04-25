package com.eikona.mata.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.eikona.mata.entity.MyUserDetails;
import com.eikona.mata.entity.User;
import com.eikona.mata.repository.UserRepository;

@Service
public class MyUserDetailsService implements UserDetailsService {
	 @Autowired
	 private UserRepository userRepository;
	 
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		Optional<User> user =Optional.of(userRepository.findByUserNameAndIsDeletedFalse(username));
    	user.orElseThrow(() -> new UsernameNotFoundException("Not Found: "+username));
    	//UserDetails userDetails =  user.map(MyUserDetails::new).get();
    	
    	return user.map(MyUserDetails::new).get();
	}
	

}
