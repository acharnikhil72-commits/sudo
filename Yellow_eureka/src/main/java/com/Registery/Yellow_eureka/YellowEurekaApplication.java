package com.Registery.Yellow_eureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class YellowEurekaApplication {

	public static void main(String[] args) {
		SpringApplication.run(YellowEurekaApplication.class, args);
	}

}
