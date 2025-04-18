package com.vaultapp.personal_data_vault;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
public class PersonalDataVaultApplication {

	public static void main(String[] args) {
		SpringApplication.run(PersonalDataVaultApplication.class, args);
	}

}
