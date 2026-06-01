package com.nec.middleware;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableJpaAuditing
@EntityScan(basePackages = {"com.nec.middleware.entity"})
@EnableJpaRepositories(basePackages = {"com.nec.middleware.repository"})
@Slf4j
public class Application {

	public static void main(String[] args) {
		log.info("Starting NEC Middleware Service Application");
		SpringApplication.run(Application.class, args);
		log.info("NEC Middleware Service Application started successfully");
	}

	@Bean
	public CommandLineRunner logStartup() {
		return args -> {
			log.info("==================================================");
			log.info("    NEC Middleware Service is now running!");
			log.info("    Swagger UI: http://localhost:8080/nec/swagger-ui.html");
			log.info("    API Docs: http://localhost:8080/nec/v3/api-docs");
			log.info("    Welcome API: http://localhost:8080/nec/");
			log.info("==================================================");
		};
	}

}
