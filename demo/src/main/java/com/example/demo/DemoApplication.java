package com.example.demo;

import java.util.Collections;
import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@RestController
@EnableSwagger2
public class DemoApplication extends WebSecurityConfigurerAdapter{

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}
	
	@GetMapping("/user")
    public Map<String, Object> user(@AuthenticationPrincipal OAuth2User principal) {
		
 		Object nameFromGit = principal.getAttribute("login");
		if(nameFromGit != null)
		{
			return Collections.singletonMap("login", principal.getAttribute("login"));
		} else
        return Collections.singletonMap("login", principal.getAttribute("given_name"));
    }
	
	
	  @Override protected void configure(HttpSecurity http) throws Exception {
		  http .authorizeRequests(a -> a 
				  .antMatchers("/","/webjars/**")
				  .permitAll() 
				  .anyRequest()
				  .authenticated())
	  
		  		.logout(l -> l 
		  				.logoutSuccessUrl("/")
		  				.permitAll() ) 
		  		.csrf(c -> c
		  				.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()) )
		  		.exceptionHandling(e -> e 
		  				.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)) ) 
		  		.oauth2Login();
		  		//.disable();
	 
	  }
	  
	  @Bean
	  public Docket api() { 
	        return new Docket(DocumentationType.SWAGGER_2)  
	          .select()                                  
	          .apis(RequestHandlerSelectors.any())              
	          .paths(PathSelectors.any())                          
	          .build();                                           
	    }

}
