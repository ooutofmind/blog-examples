package com.oooutofmind.springgatewaycspnonce;


import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.cloud.gateway.filter.factory.SecureHeadersGatewayFilterFactory.CONTENT_SECURITY_POLICY_HEADER;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.StreamUtils;

@SpringBootTest
@AutoConfigureWebTestClient
public class GatewayIntegrationTest {

  @Autowired
  WebTestClient webTestClient;

  @Test
  public void testWelcomePage() throws Exception {

    var exchangeResult = webTestClient.get()
        .uri("/")
        .exchange()
        .expectStatus().isOk()
        .expectHeader().valueEquals(HttpHeaders.CONTENT_TYPE, "text/html; charset=utf-8")
        .expectHeader().value(CONTENT_SECURITY_POLICY_HEADER, Matchers.stringContainsInOrder("'nonce-", "'nonce-"))
        .returnResult(String.class);

    byte[] compressedBody = exchangeResult.getResponseBodyContent();
    assertThat(compressedBody).isNotNull();

    try (var out = new ByteArrayOutputStream();
        var in = new GZIPInputStream(new ByteArrayInputStream(exchangeResult.getResponseBodyContent()))) {
      StreamUtils.copy(in, out);

      String responseBody = out.toString(StandardCharsets.UTF_8);
      assertThat(responseBody).doesNotContain("<script nonce=\"${random_nonce}\">");
    }

  }

}