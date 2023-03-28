package com.oooutofmind.springgatewaycspnonce.config;

import static org.springframework.cloud.gateway.filter.factory.SecureHeadersGatewayFilterFactory.CONTENT_SECURITY_POLICY_HEADER;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.jsoup.Jsoup;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.factory.rewrite.RewriteFunction;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public class NonceRewriteFunction implements RewriteFunction<String, String> {

  public static final String NONCE_ATTR = "nonce";
  private static final int NONCE_LENGTH = 128;
  public static final String NONCE_CSS_QUERY = String.format("[%s=${random_nonce}]", NONCE_ATTR);
  private final SecureRandom secureRandom = new SecureRandom();

  @Override
  public Publisher<String> apply(ServerWebExchange exchange, String responseBody) {
    var doc = Jsoup.parse(responseBody);

    Set<String> nonceSet = doc.select(NONCE_CSS_QUERY).stream()
        .map(el -> {
          var nonce = nonce();
          el.attr(NONCE_ATTR, nonce);
          return nonce;
        })
        .collect(Collectors.toSet());

    replaceCspHeaderWithGeneratedNonce(exchange, nonceSet);

    return Mono.just(doc.html());
  }

  private void replaceCspHeaderWithGeneratedNonce(
      ServerWebExchange exchange,
      Set<String> nonceList) {

    ServerHttpResponse response = exchange.getResponse();
    String nonceString = nonceList.stream()
        .map(it -> String.format("'nonce-%s'", it))
        .collect(Collectors.joining(" "));

    Optional.ofNullable(response.getHeaders().getFirst(CONTENT_SECURITY_POLICY_HEADER))
        .ifPresent(csp -> {
          var cspList = Arrays.stream(csp.split(";"))
              .map(String::trim)
              .map(entry -> {
                if (entry.startsWith("script-src")) {
                  entry += " " + nonceString;
                }

                return entry;
              })
              .collect(Collectors.joining(";"));
          response.getHeaders().set(CONTENT_SECURITY_POLICY_HEADER, cspList);
        });
  }

  private String nonce() {
    byte[] nonceArray = new byte[NONCE_LENGTH];

    secureRandom.nextBytes(nonceArray);

    return Base64.getEncoder().encodeToString(nonceArray);
  }
}
