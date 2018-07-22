package com.github.rshindo.stock;

import java.time.Duration;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.Data;
import reactor.core.publisher.Mono;

@SpringBootApplication
@RestController
@RequestMapping("stock")
public class StockApplication {

    public static void main(String[] args) {
        SpringApplication.run(StockApplication.class, args);
    }

    @Bean
    WebClient webClient() {
        return WebClient.create("localhost:8080");
    }

    @Autowired
    WebClient webClient;

    @GetMapping("/{id}")
    Mono<Stock> get(@PathVariable("id") Integer id) {

        Stock stock = new Stock();
        stock.id = id;
        stock.stock = stockData.get(id);
        return Mono.just(stock)
                .delaySubscription(Duration.ofSeconds(1));
    }

    Map<Integer, Integer> stockData = Map.ofEntries(Map.entry(1, 10), Map.entry(2, 20), Map.entry(3, 30),
            Map.entry(4, 40), Map.entry(5, 50));

    @Data
    public static class Stock {
        private int id;
        private int stock;
    }

}
