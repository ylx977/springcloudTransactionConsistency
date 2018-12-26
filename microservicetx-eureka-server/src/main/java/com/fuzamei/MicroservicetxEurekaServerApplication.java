package com.fuzamei;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@EnableEurekaServer
@SpringBootApplication
public class MicroservicetxEurekaServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(MicroservicetxEurekaServerApplication.class, args);
	}
}
