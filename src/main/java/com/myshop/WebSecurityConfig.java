package com.myshop;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.myshop.repository.UserRepository;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	public static final String ADMIN_AUTHORITY = "ADMIN";
	
	@Autowired UserRepository userRepo;
	
	@Override
	protected void configure(HttpSecurity httpSecurity) throws Exception {
		httpSecurity.csrf().disable()
			.authorizeRequests().antMatchers("/admin/**").hasAuthority(ADMIN_AUTHORITY)
				.antMatchers("/user/**").authenticated()
				.antMatchers("/*").permitAll().and()
			.formLogin().loginPage("/login").permitAll().and()
			.logout().permitAll();
	}
	
	
}
