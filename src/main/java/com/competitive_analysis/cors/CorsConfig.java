package com.competitive_analysis.cors;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(
                        "web-compare-sigma.vercel.app",
                        "web-compare-ogkscmk6z-umesh-tiwaris-projects-5a6ac068.vercel.app",
                        "web-compare-git-main-umesh-tiwaris-projects-5a6ac068.vercel.app",
                        "http://localhost:5173"
                )
                .allowedMethods("*")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}