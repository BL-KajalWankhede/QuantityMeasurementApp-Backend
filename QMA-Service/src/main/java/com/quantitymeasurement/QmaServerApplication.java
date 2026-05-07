package com.quantitymeasurement;
 
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
 
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class QmaServerApplication {
 
    public static void main(String[] args) {
        SpringApplication.run(QmaServerApplication.class, args);
    }
 
}
