package org.itentika.edu.spuzakov.mvc.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.itentika.edu.spuzakov.mvc.converter.ClientClientDto;
import org.itentika.edu.spuzakov.mvc.converter.OrderDtoOrder;
import org.itentika.edu.spuzakov.mvc.converter.OrderOrderDto;
import org.itentika.edu.spuzakov.mvc.dto.OrderDto;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.text.SimpleDateFormat;
import java.util.List;

@Configuration
public class WebConfig extends WebMvcConfigurationSupport {
    @Bean
    public ObjectMapper objectMapper() {
        Jackson2ObjectMapperBuilder builder = Jackson2ObjectMapperBuilder.json();
        if (this.getApplicationContext() != null) {
            builder.applicationContext(this.getApplicationContext());
        }
        ObjectMapper mapper = builder.build();
        mapper.setDateFormat(new SimpleDateFormat("dd.MM.yyyy hh:mm"));
        return mapper;
    }

    @Override
    protected void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter(objectMapper());
        converter.registerObjectMappersForType(OrderDto.class, m -> m.put(MediaType.APPLICATION_JSON, objectMapper()));
        converters.add(converter);
    }

    @Override
    protected void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new ClientClientDto());
        registry.addConverter(new OrderOrderDto());
        registry.addConverter(new OrderDtoOrder());
        super.addFormatters(registry);
    }
}
