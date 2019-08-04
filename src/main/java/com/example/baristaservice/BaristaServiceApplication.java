package com.example.baristaservice;

import com.example.baristaservice.integration.Waiter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
@EnableDiscoveryClient
@EnableBinding(Waiter.class)
public class BaristaServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(BaristaServiceApplication.class, args);
	}

}
