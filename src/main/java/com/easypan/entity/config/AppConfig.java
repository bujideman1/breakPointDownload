package com.easypan.entity.config;


import lombok.Getter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component("appConfig")
public class AppConfig {
    @Value("${project.folder:}")
    private String projectFolder;
}
