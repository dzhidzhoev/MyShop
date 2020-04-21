package com.myshop;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import com.myshop.repository.UserRepository;

@Component
public class ShopAuthProvider implements AuthenticationProvider {
	@Autowired UserRepository userRepo;
	
	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		String email = authentication.getName();
		String pwd = authentication.getCredentials().toString();
		var user = userRepo.logIn(email, pwd);
		if (user.isPresent()) {
			var principal = new ShopUserPrincipal(user.get());
			return new UsernamePasswordAuthenticationToken(principal, pwd, principal.getAuthorities());
		} else {
			throw new AuthenticationCredentialsNotFoundException(email);
		}
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return authentication.equals(UsernamePasswordAuthenticationToken.class);
	}

}
