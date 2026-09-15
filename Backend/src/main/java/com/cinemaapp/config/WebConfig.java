package com.cinemaapp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.upload.dir}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/resources/**")
                .addResourceLocations("classpath:/static/");

        Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(uploadPath);
        } catch (IOException e) {
            throw new IllegalStateException("Não foi possível criar o diretório de uploads: " + uploadPath, e);
        }
        // Ordem importa: primeiro tenta a pasta de uploads em disco (posteres enviados
        // pelo admin); se não achar, cai para o classpath, onde vive o default-avatar.jpg
        // empacotado no jar. Sem o segundo local, o avatar padrão fica quebrado.
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadPath + "/", "classpath:/static/uploads/");
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("redirect:/filmes");
        registry.addViewController("/login").setViewName("redirect:/auth/login");
    }
}