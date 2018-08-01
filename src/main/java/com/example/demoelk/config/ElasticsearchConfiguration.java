package com.example.demoelk.config;

import com.example.demoelk.common.mapper.IMovieCustomMapper;
import com.example.demoelk.common.mapper.MovieCustomMapper;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.UnknownHostException;

/**
 * Created by damian on 30.07.18.
 */
@Configuration
public class ElasticsearchConfiguration {

    @Value("${elastic.host}")
    private String host;

    @Value("${elastic.port}")
    private Integer port;

    @Value("${elastic.protocol}")
    private String protocol;


    @Bean
    IMovieCustomMapper iMovieCustomMapper() {
        return new MovieCustomMapper();
    }

    @Bean
    RestHighLevelClient client() throws UnknownHostException {
        return new RestHighLevelClient(
                RestClient.builder(new HttpHost(
                        host,
                        port,
                        protocol)));
    }
}
