package com.dxp.ui.application.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class DXPConfiguration {

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

    @Value("${categorization}")
    private String categorizationEndpoint;

    @Value("${categorizationV2}")
    private String categorizationEndpointV2;

    @Value("${cpa}")
    private String cpaEndpoint;
    public String getCategorizationEndpoint() {
        return categorizationEndpoint;
    }

    public void setCategorizationEndpoint(String categorizationEndpoint) {
        this.categorizationEndpoint = categorizationEndpoint;
    }

    public String getCpaEndpoint() {
        return cpaEndpoint;
    }

    public void setCpaEndpoint(String cpaEndpoint) {
        this.cpaEndpoint = cpaEndpoint;
    }

    public String getCategorizationEndpointV2() {
        return categorizationEndpointV2;
    }



}
