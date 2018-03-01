package com.excell.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.*")
public class ExcellToJavaApplication {
	public static void main(String[] args) {
		SpringApplication.run(ExcellToJavaApplication.class, args);
	}
}
