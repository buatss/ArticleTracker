package com.buatss;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan("com.buatss")
@EnableJpaRepositories("com.buatss")
public class ArticleTrackerApplication {
	public static void main(String[] args) {
		SpringApplication.run(ArticleTrackerApplication.class, args);
	}
}
