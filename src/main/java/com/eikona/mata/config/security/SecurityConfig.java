package com.eikona.mata.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.eikona.mata.service.MyUserDetailsService;


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
	
	@Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
	
	@Order(1)
	@Configuration
	public static class RestConfiguration extends WebSecurityConfigurerAdapter {
		@Autowired
		private MyUserDetailsService myUserDetailsService;
		
		@Autowired
		private JwtRequestFilter jwtRequestFilter;
		
		@Override
		protected void configure(AuthenticationManagerBuilder auth)throws Exception{
			auth.userDetailsService(myUserDetailsService);
		}

		@Override
		protected void configure(HttpSecurity http) throws Exception{
			http.authorizeRequests().antMatchers(HttpMethod.POST, "/LAPI/**" ,"/Subscribe/**","/corsight/**","/hfsecurity/**").permitAll();
			http.antMatcher("/rest/**")
			.cors()
            .and()
		    .csrf()
		        .disable() // we don't need CSRF because our token is invulnerable
		        .authorizeRequests()
				.antMatchers("/rest/authenticate").permitAll()
				.anyRequest().authenticated()
				.and().sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
			http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
			http.exceptionHandling().authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED));
		}
		@Override
		@Bean
		public AuthenticationManager authenticationManagerBean() throws Exception{
			return super.authenticationManagerBean();
		}

	}
	
//	@Order(2)
//	@Configuration
//	public static class ListenerConfiguration extends WebSecurityConfigurerAdapter {
//		
//
//		@Override
//		protected void configure(HttpSecurity http) throws Exception{
//			http.antMatcher("/").authorizeRequests().antMatchers(HttpMethod.POST, "/LAPI/**" ,"/Subscribe/**","/").permitAll();
//			
//		}
//		
//
//	}

	@Order(3)
	@Configuration
	public static class WebConfiguration extends WebSecurityConfigurerAdapter {
		@Autowired
		private MyUserDetailsService myUserDetailsService;
		
		
		@Override
		protected void configure(AuthenticationManagerBuilder auth)throws Exception{
			auth.authenticationProvider(authenticationProvider());
		}
		@Bean
	    DaoAuthenticationProvider authenticationProvider(){
	        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
	        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
	        daoAuthenticationProvider.setUserDetailsService(this.myUserDetailsService);

	        return daoAuthenticationProvider;
	    }

		 @Override
		    protected void configure(HttpSecurity http) throws Exception {
 				  http
	              .antMatcher("/**")
	              .csrf().disable()
	              .authorizeRequests()
              			.antMatchers(HttpMethod.POST, "/LAPI/**" ,"/Subscribe/**","/corsight/**","/hfsecurity/**").permitAll()
		            	.antMatchers("/resources/**", "/webjars/**","/assets/**","/api/**").permitAll()
		                .anyRequest().authenticated()
		                .and()
		            .formLogin()
		                .loginPage("/login")
		                .defaultSuccessUrl("/organization")
		                .failureUrl("/login?error")
		                .permitAll()
		                .and()
		            .logout()
		            	.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
		            	.logoutSuccessUrl("/login?logout")
		                .permitAll()
		             	.and()
		            .exceptionHandling().accessDeniedPage("/403")
		             	;
	}

	}
	
	
	
}


