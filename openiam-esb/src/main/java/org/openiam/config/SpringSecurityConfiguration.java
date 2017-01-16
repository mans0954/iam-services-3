package org.openiam.config;

import java.util.Arrays;

import org.openiam.security.OpenIAMAuthenticationDetailsSource;
import org.openiam.security.OpenIAMAuthenticationUserDetailsService;
import org.openiam.security.OpenIAMPreAuthFilter;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;

@Configurable
@EnableWebSecurity
//@EnableGlobalMethodSecurity(securedEnabled=true, prePostEnabled=true)
public class SpringSecurityConfiguration extends WebSecurityConfigurerAdapter {

	@Override
    protected void configure(final HttpSecurity http) throws Exception {
		http.csrf().disable().authorizeRequests().antMatchers("/**").permitAll();
		
		http.addFilterAfter(preAuthFilter(), SecurityContextPersistenceFilter.class);
		
		/* 
		 * inheritable thread local is evil. This will *not* work when submitting to executors, for example,
		 * as threads are re-used
		 */
		//SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);
	}
	
	@Bean
	public OpenIAMPreAuthFilter preAuthFilter() {
		final OpenIAMPreAuthFilter filter = new OpenIAMPreAuthFilter();
		filter.setAuthenticationDetailsSource(authenticationDetailsSource());
		filter.setAuthenticationManager(authenticationManager());
		return filter;
	}
	
	@Bean
	public AuthenticationManager authenticationManager() {
		final ProviderManager manager = new ProviderManager(Arrays.asList(new AuthenticationProvider[] {
			authenticationProvider()
		}));
		return manager;
	}
	
	@Bean
	public AuthenticationProvider authenticationProvider() {
		final PreAuthenticatedAuthenticationProvider provider = new PreAuthenticatedAuthenticationProvider();
		provider.setPreAuthenticatedUserDetailsService(detailsService());
		return provider;
	}
	
	@Bean
	public OpenIAMAuthenticationUserDetailsService detailsService() {
		return new OpenIAMAuthenticationUserDetailsService();
	}
	
	@Bean
	public OpenIAMAuthenticationDetailsSource authenticationDetailsSource() {
		final OpenIAMAuthenticationDetailsSource source = new OpenIAMAuthenticationDetailsSource();
		return source;
	}
}
