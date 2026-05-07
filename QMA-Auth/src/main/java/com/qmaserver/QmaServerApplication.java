package com.qmaserver;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class QmaServerApplication {

    public static void main(String[] args) {
        System.out.println(">>> QMA-AUTH STARTING WITH LATEST CODE VERSION 3 <<<");
        SpringApplication.run(QmaServerApplication.class, args);
    }

}
