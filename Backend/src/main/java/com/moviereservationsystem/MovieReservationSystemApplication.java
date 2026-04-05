package com.moviereservationsystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
public class MovieReservationSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(MovieReservationSystemApplication.class, args);
        System.out.println("Application Started");
	}

}
