package com.sergeineretin.cloudfilestorage.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringConfig {

    @Bean
    ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
