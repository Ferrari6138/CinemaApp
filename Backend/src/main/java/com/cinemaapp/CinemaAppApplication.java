package com.cinemaapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CinemaAppApplication {
	public static void main(String[] args) {
		SpringApplication.run(CinemaAppApplication.class, args);
	}
}
