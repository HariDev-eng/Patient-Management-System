package com.pm.patientservice.kafka;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaDebug {

    private final KafkaProperties kafkaProperties;

    @PostConstruct
    public void init() {
        System.out.println("==================================");
        System.out.println("Kafka bootstrap servers = " + kafkaProperties.getBootstrapServers());
        System.out.println("Producer properties = " + kafkaProperties.buildProducerProperties(null));
        System.out.println("==================================");
    }
}