package com.oooutofmind.springgatewaycspnonce.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.factory.rewrite.RewriteFunction;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

@Configuration
public class RouteConfiguration {

  RewriteFunction<String, String> nonceRewriteFunction = new NonceRewriteFunction();

  @Bean
    public RouteLocator routes(RouteLocatorBuilder builder,
      @Value("${app.ui-service.uri}") String uiServiceUri,
      @Value("${app.ui-service.index}") String uiServiceIndexPage) {

    return builder
        .routes()
        .route("welcome_page_route", r ->
            r.method(HttpMethod.GET)
                .and()
                .path("/")
                .filters(
                    f -> f
                        .secureHeaders(h -> h
                            .setContentSecurityPolicy("default-src 'self'; script-src 'self';"))
                        .setRequestHeader(HttpHeaders.ACCEPT_ENCODING, "gzip")
                        .rewritePath("/", uiServiceIndexPage)
                        .modifyResponseBody(String.class, String.class, nonceRewriteFunction))
                .uri(uiServiceUri))
        .build();
  }
}
