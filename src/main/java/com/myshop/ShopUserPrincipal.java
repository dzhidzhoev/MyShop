package com.myshop;

import java.util.Collection;
import java.util.List;

import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.myshop.model.User;
import com.sun.security.auth.UserPrincipal;

public class ShopUserPrincipal implements UserDetails {
	private static final long serialVersionUID = 1L;
	private User user;
	
	public ShopUserPrincipal(User user) {
		
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return Boolean.valueOf(true).equals(user.isAdminOrNull()) 
				? List.of(new GrantedAuthority() {
					private static final long serialVersionUID = 1L;

					@Override
					public String getAuthority() {
						return WebSecurityConfig.ADMIN_AUTHORITY;
					}
					
				}) 
				: List.of();
	}

	@Override
	public String getPassword() {
		return user.getPwdHash();
	}

	@Override
	public String getUsername() {
		return user.getEmail();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return !user.isDeleted();
	}
	
}
