package com.eikona.mata.config.content;


import org.springframework.content.fs.config.FilesystemStoreConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterRegistry;

import com.eikona.mata.entity.FileSystemContent;


@Configuration
public class StoreConfig {
    @Bean
    public FilesystemStoreConfigurer configurer() {
        return new FilesystemStoreConfigurer() {

            @Override
            public void configureFilesystemStoreConverters(ConverterRegistry registry) {
                registry.addConverter(new Converter<FileSystemContent, String>() {

                    @Override
                    public String convert(FileSystemContent document) {
                        return document.getContentPath();
                    }
                });
            }
        };
    }

   
}