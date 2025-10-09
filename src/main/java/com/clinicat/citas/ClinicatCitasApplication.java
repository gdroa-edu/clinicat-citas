package com.clinicat.citas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = {"clinicat.commons.entity"})
@EnableJpaRepositories(basePackages = {"com.clinicat.citas.repository"})
public class ClinicatCitasApplication {

	public static void main(String[] args) {
		SpringApplication.run(ClinicatCitasApplication.class, args);
	}

}
