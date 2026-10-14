package se.iths.sofia.webshopgateway.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        CorsRegistration registration = registry.addMapping("/**");

        registration.allowedOrigins("http://localhost:5173", "http://localhost:5174","http://localhost:5175");
        registration.allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS");
        registration.allowedHeaders("Content-Type", "Authorization");
        registration.allowCredentials(true);
    }
}
