package com.softnerve.epic.configuration.epicConfig;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Date;

@Component
public class OffsetDateTimeReadConverter
        implements Converter<Date, OffsetDateTime> {

    @Override
    public OffsetDateTime convert(Date source) {
        return source == null
                ? null
                : source.toInstant().atOffset(ZoneOffset.UTC);
    }
}
