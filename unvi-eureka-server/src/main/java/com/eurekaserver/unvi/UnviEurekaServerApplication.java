package com.eurekaserver.unvi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class UnviEurekaServerApplication {

  public static void main(String[] args) {
    SpringApplication.run(UnviEurekaServerApplication.class, args);
  }

}
