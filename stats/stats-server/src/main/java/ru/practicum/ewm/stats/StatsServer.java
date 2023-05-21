package ru.practicum.ewm.stats;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableTransactionManagement
@EntityScan
@SpringBootApplication
public class StatsServer {

    public static void main(String[] args) {
        SpringApplication.run(StatsServer.class, args);
    }

}
