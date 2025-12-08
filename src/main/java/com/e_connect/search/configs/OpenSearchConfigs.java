package com.e_connect.search.configs;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.opensearch.client.RestClient;

import org.opensearch.client.json.jackson.JacksonJsonpMapper;
import org.opensearch.client.transport.rest_client.RestClientTransport;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.transport.OpenSearchTransport;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class OpenSearchConfigs {

  @Value("${opensearch.host.user.name}")
  private String userName;
  @Value("${opensearch.host.password}")
  private String password;
  @Value("${opensearch.host.path}")
  private String hostURL;
  @Value("${opensearch.host.port}")
  private int hostPort;
  @Value("${opensearch.host.schema}")
  private String hostSchema;

  @Bean
  public OpenSearchClient openSearchClient() throws Exception {

    final HttpHost host = new HttpHost(hostURL, hostPort, hostSchema);
    final BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
    credentialsProvider.setCredentials(new AuthScope(host), new UsernamePasswordCredentials(userName, password));

    final RestClient restClient = RestClient.builder(host)
        .setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder
            .setDefaultCredentialsProvider(credentialsProvider))
        .build();

    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    final OpenSearchTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper(objectMapper));
    return new OpenSearchClient(transport);
  }
}
