package com.project.gestao_sala;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class GestaoSalaApplication {

	public static void main(String[] args) {
		SpringApplication.run(GestaoSalaApplication.class, args);
	}

}
