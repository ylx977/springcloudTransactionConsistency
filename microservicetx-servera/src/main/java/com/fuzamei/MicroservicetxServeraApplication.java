package com.fuzamei;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@EnableEurekaClient
@SpringBootApplication
@EnableFeignClients
public class MicroservicetxServeraApplication {

	public static void main(String[] args) {
		SpringApplication.run(MicroservicetxServeraApplication.class, args);
	}
}
