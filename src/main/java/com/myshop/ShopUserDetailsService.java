package com.myshop;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.myshop.model.User;
import com.myshop.ShopUserPrincipal;
import com.myshop.repository.UserRepository;

import jdk.jshell.spi.ExecutionControl.UserException;

@Service
public class ShopUserDetailsService implements UserDetailsService {
	@Autowired UserRepository userRepo;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		var user = userRepo.findByEmailIgnoreCase(username);
		if (user.isEmpty()) {
			throw new UsernameNotFoundException(username);
		}
		return new ShopUserPrincipal(user.get());
	}

}
