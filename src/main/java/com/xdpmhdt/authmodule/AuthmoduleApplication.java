package com.xdpmhdt.authmodule;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Carbon Credit Marketplace Authentication Module
 * 
 * Main Spring Boot Application for authentication and authorization
 * Supporting roles: EV_OWNER, CC_BUYER, CVA, ADMIN
 */
@SpringBootApplication
public class AuthmoduleApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuthmoduleApplication.class, args);
	}

}
