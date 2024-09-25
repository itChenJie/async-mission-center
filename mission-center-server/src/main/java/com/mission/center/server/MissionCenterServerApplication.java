package com.mission.center.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;


@SpringBootApplication
@ComponentScan("com.mission.center")
@EnableEurekaClient
@EnableAsync
@EnableFeignClients
public class MissionCenterServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(MissionCenterServerApplication.class, args);
    }

}
