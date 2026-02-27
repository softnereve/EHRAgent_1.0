package com.softnerve.epic.configuration.epicConfig;

import org.attachment.softnerve.service.KafkaService;
import org.attachment.softnerve.service.KafkaServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class BeanConfig {

    //Kafka Configuration
    @Bean
    public KafkaService kafkaService(){
        return new KafkaServiceImpl();
    }

    @Bean
    public WebClient webClient(WebClient.Builder builder) {
        return builder.build();
    }
}
