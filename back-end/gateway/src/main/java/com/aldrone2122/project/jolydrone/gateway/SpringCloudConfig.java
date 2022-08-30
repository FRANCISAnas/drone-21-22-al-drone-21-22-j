package com.aldrone2122.project.jolydrone.gateway;


import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.logging.Logger;

import com.aldrone2122.project.jolydrone.gateway.entity.Coordinates;
import com.aldrone2122.project.jolydrone.gateway.entity.Delivery;
import com.aldrone2122.project.jolydrone.gateway.entity.Region;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@Configuration
public class SpringCloudConfig {

    private static final String FLIGHT_MONITOR_PATTERN = "/flight-monitor/**";

    private static final String FLIGHT_MONITOR = "flightMonitorService";

    private static final int NB_RETRIES = 20;
    private static final long DURATION_BETWEEN_RETRIES = 5;
    private static final long TIMEOUT_BETWEEN_RETRIES = 7;
    private final ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());
    private final ObjectMapper objectMapper;
    private Logger logger = Logger.getLogger(SpringCloudConfig.class.getName());
    @Value("${flight.monitor.url.derenhalle}")
    private String uriDernhalle;

    @Value("${flight.monitor.url.tiscus}")
    private String uriTiscus;

    @Value("${flight.monitor.url.sylent}")
    private String uriSylent;

    @Value("${flight.monitor.url.verson}")
    private String uriVerson;

    @Value("${flight.monitor.url.quendaylon}")
    private String uriQuendaylon;

    private Map<String, Region> regionsMap;

    @Autowired
    public SpringCloudConfig(Jackson2ObjectMapperBuilder builder) {
        this.objectMapper = builder.build();
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) throws Exception {

        this.init();

        return builder.routes()
                .route("flight-monitor-derenhalle", r ->
                        r.path(FLIGHT_MONITOR_PATTERN)
                                .and()
                                .readBody(String.class, body -> isInRegion(body, "derenhalle"))
                                .and()
                                .method("POST", "GET")
                                .filters(f -> f.retry(retryConfig -> {
                                    retryConfig.setBackoff(Duration.ofSeconds(DURATION_BETWEEN_RETRIES), Duration.ofSeconds(TIMEOUT_BETWEEN_RETRIES), 1, false);
                                    retryConfig.setRetries(NB_RETRIES).setMethods(HttpMethod.GET, HttpMethod.POST);
                                    retryConfig.validate();
                                }))
                                .uri(uriDernhalle))
                .route("flight-monitor-tiscus", r ->
                        r.path(FLIGHT_MONITOR_PATTERN)
                                .and()
                                .readBody(String.class, body -> isInRegion(body, "tiscus"))
                                .and()
                                .method("POST", "GET")
                                .filters(f -> f.retry(retryConfig -> {
                                    retryConfig.setBackoff(Duration.ofSeconds(DURATION_BETWEEN_RETRIES), Duration.ofSeconds(TIMEOUT_BETWEEN_RETRIES), 1, false);
                                    retryConfig.setRetries(NB_RETRIES).setMethods(HttpMethod.GET, HttpMethod.POST);
                                    retryConfig.validate();
                                }))
                                .uri(uriTiscus))
                .route("flight-monitor-sylent", r ->
                        r.path(FLIGHT_MONITOR_PATTERN)
                                .and()
                                .readBody(String.class, body -> isInRegion(body, "sylent"))
                                .and()
                                .method("POST", "GET")
                                .filters(f -> f.retry(retryConfig -> {
                                    retryConfig.setBackoff(Duration.ofSeconds(DURATION_BETWEEN_RETRIES), Duration.ofSeconds(TIMEOUT_BETWEEN_RETRIES), 1, false);
                                    retryConfig.setRetries(NB_RETRIES).setMethods(HttpMethod.GET, HttpMethod.POST);
                                    retryConfig.validate();
                                }))
                                .uri(uriSylent))

                .route("flight-monitor-verson", r ->
                        r.path(FLIGHT_MONITOR_PATTERN)
                                .and()
                                .readBody(String.class, body -> isInRegion(body, "verson"))
                                .and()
                                .method("POST", "GET")
                                .filters(f -> f.retry(retryConfig -> {
                                    retryConfig.setBackoff(Duration.ofSeconds(DURATION_BETWEEN_RETRIES), Duration.ofSeconds(TIMEOUT_BETWEEN_RETRIES), 1, false);
                                    retryConfig.setRetries(NB_RETRIES).setMethods(HttpMethod.GET, HttpMethod.POST);
                                    retryConfig.validate();
                                }))
                                .uri(uriVerson))
                .route("flight-monitor-quendaylon", r ->
                        r.path(FLIGHT_MONITOR_PATTERN)
                                .and()
                                .readBody(String.class, body -> isInRegion(body, "quendaylon"))
                                .and()
                                .method("POST", "GET")
                                .filters(f -> f.retry(retryConfig -> {
                                    retryConfig.setBackoff(Duration.ofSeconds(DURATION_BETWEEN_RETRIES), Duration.ofSeconds(TIMEOUT_BETWEEN_RETRIES), 1, false);
                                    retryConfig.setRetries(NB_RETRIES).setMethods(HttpMethod.GET, HttpMethod.POST);
                                    retryConfig.validate();
                                }))
                                .uri(uriQuendaylon))
                .build();
    }

    private boolean isInRegion(String body, String regionName) {
        try {

            var deliveryDTO = this.objectMapper.readValue(body, Delivery.class);
            Coordinates origin = deliveryDTO.getFlightPlan().getOrigin();

            if (origin == null) return false;

            for (Map.Entry<String, Region> value : this.regionsMap.entrySet()) {
                if (value.getKey().equals(regionName)) {
                    return origin.isInRegion(value.getValue());
                }
            }
            return false;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void init() throws IOException {

        this.regionsMap = this.yamlMapper.readValue(
                SpringCloudConfig.class.getClassLoader().getResourceAsStream("map.yaml"),
                new TypeReference<>() {
                });

        this.regionsMap.forEach((regionName, value) -> value.setName(regionName));

    }

}
