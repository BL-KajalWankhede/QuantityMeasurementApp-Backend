package com.qmaserver;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class QmaServerApplication {
    private static final Logger log = LogManager.getLogger(QmaServerApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(QmaServerApplication.class, args);
        log.info("QMA-Auth Service started successfully!");
    }

}
