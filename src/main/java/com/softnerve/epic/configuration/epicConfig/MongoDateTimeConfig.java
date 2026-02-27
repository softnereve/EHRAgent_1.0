package com.softnerve.epic.configuration.epicConfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

import java.util.List;

@Configuration
public class MongoDateTimeConfig {

    @Bean
    public MongoCustomConversions mongoCustomConversions(
            OffsetDateTimeWriteConverter writeConverter,
            OffsetDateTimeReadConverter readConverter) {

        return new MongoCustomConversions(
                List.of(writeConverter, readConverter)
        );
    }
}
