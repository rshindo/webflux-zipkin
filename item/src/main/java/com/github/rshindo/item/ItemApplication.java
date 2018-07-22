package com.github.rshindo.item;

import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.AllArgsConstructor;
import lombok.Data;
import reactor.core.publisher.Flux;
import reactor.util.function.Tuples;

@SpringBootApplication
@RestController
@RequestMapping("/item")
public class ItemApplication {

    public static void main(String[] args) {
        SpringApplication.run(ItemApplication.class, args);
    }

    @Bean
    WebClient webClient() {
        return WebClient.create("http://localhost:8081");
    }

    @Autowired
    WebClient webClient;

    @GetMapping("/list")
    Flux<Item> list() {
        return Flux.fromStream(items())
                .flatMap(item -> {
                    return webClient.get().uri("/stock/{id}", item.id)
                            .retrieve()
                            .bodyToMono(Stock.class)
                            .map(stock -> Tuples.of(item, stock));
                }).map(t -> {
                    Item item = t.getT1();
                    item.stock = t.getT2().stock;
                    return item;
                });
    }

    Stream<Item> items() {
        return Stream.of(new Item(1, "Beer", null),
                new Item(2, "coke", null),
                new Item(3, "tea", null),
                new Item(4, "green tea", null),
                new Item(5, "wine", null));
    }

    @Data
    @AllArgsConstructor
    public static class Item {
        private int id;
        private String name;
        private Integer stock;
    }

    @Data
    public static class Stock {
        private int id;
        private int stock;
    }
}
