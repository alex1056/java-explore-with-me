package ru.practicum.ewm.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableJpaRepositories
@EnableTransactionManagement
@EntityScan
@SpringBootApplication(scanBasePackages = {"ru.practicum.ewm"})
public class MainServer {

    public static void main(String[] args) {
        SpringApplication.run(MainServer.class, args);
    }

}
