package com.shravani.CandyDistributor.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI candyDistributorOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("Candy Distributor API")
                        .description("API documentation for Candy Distributor system")
                        .version("1.0"));
    }
}
