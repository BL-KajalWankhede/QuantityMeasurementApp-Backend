package com.qmaapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class QmaApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(QmaApiApplication.class, args);
    }
}
