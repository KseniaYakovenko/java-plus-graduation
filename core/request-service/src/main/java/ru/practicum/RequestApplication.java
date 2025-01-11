package ru.practicum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import ru.practicum.client.EventServiceClient;
import ru.practicum.client.UserServiceClient;

@SpringBootApplication
@EnableFeignClients(clients = {EventServiceClient.class, UserServiceClient.class})
public class RequestApplication {
    public static void main(String[] args) {
        SpringApplication.run(RequestApplication.class, args);
    }
}