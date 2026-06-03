package com.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

//Example Feign Client demonstrating Interservice Communication.
//allows QMA-Service to call QMA-Auth using Eureka to resolve the IP address.

@FeignClient(name = "qma-auth")
public interface AuthServiceClient {

    @GetMapping("/api/v1/users/me")
    Object getCurrentUser(@RequestHeader("Authorization") String token);

}
