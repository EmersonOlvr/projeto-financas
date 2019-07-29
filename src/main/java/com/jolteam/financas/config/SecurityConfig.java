package com.jolteam.financas.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.client.web.HttpSessionOAuth2AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;

import com.jolteam.financas.oauth2.CustomAuthenticationFailureHandler;
import com.jolteam.financas.oauth2.CustomAuthenticationSuccessHandler;
import com.jolteam.financas.oauth2.CustomOAuth2UserService;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter implements ApplicationContextAware {
	
	@Autowired private CustomOAuth2UserService customOAuth2UserService;
	@Autowired private CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;
	@Autowired private CustomAuthenticationFailureHandler customAuthenticationFailureHandler;
	
	@Bean
	public AuthorizationRequestRepository<OAuth2AuthorizationRequest> customAuthorizationRequestRepository() {
		System.out.println("customAuthorizationRequestRepository()");
		
		return new HttpSessionOAuth2AuthorizationRequestRepository();
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		System.out.println("configure()");
		
		http
			.oauth2Login()
				.loginPage("/entrar")
				.authorizationEndpoint()
					.baseUri("/oauth2/authorize")
					.authorizationRequestRepository(this.customAuthorizationRequestRepository())
					.and()
				.redirectionEndpoint()
					.baseUri("/oauth2/callback/*")
					.and()
				.userInfoEndpoint()
					.userService(this.customOAuth2UserService)
					.and()
				.successHandler(this.customAuthenticationSuccessHandler)
				.failureHandler(this.customAuthenticationFailureHandler);
	}

}
