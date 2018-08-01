package com.example.demoelk.core.movie;

import com.example.demoelk.common.dto.MovieDTO;
import com.example.demoelk.common.exception.SearchValidationException;
import com.example.demoelk.common.mapper.IMovieCustomMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang.StringUtils;
import org.apache.http.Header;
import org.apache.lucene.util.CollectionUtil;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Transactional
@Service
public class MovieService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MovieService.class);

    private final IMovieCustomMapper movieMapper;

    private final RestHighLevelClient elasticSearchClient;


    @Autowired
    public MovieService(IMovieCustomMapper movieMapper,
                        RestHighLevelClient elasticSearchClient) {
        this.movieMapper = movieMapper;
        this.elasticSearchClient = elasticSearchClient;
    }

    public Page<MovieDTO> searchByTitle(String title) {
        LOGGER.info("About to search movie by title");

        if (StringUtils.isBlank(title)) {
            throw new SearchValidationException("Title is blank");
        }

        SearchRequest searchRequest = extractSearchRequest("title", title);
        return searchAndMapResults(searchRequest);
    }

    public Page<MovieDTO> searchByGenre(String genre) {
        LOGGER.info("About to search movie by genre");

        if (StringUtils.isBlank(genre)) {
            throw new SearchValidationException("Genre is blank");
        }

        SearchRequest searchRequest = extractSearchRequest("genre", genre);
        return searchAndMapResults(searchRequest);
    }

    public Page<MovieDTO> searchAll() {
        LOGGER.info("About to search all movies");

        SearchRequest searchRequest = new SearchRequest("movies");
        searchRequest.types("doc");

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(QueryBuilders.matchAllQuery());
        sourceBuilder.from(0);
        sourceBuilder.size(50);
        sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));

        return searchAndMapResults(searchRequest);
    }

    private Page<MovieDTO> searchAndMapResults(SearchRequest searchRequest) {
        LOGGER.info("Before searching: {}", searchRequest);
        SearchResponse searchResponse = generateResponse(searchRequest);

        LOGGER.info("Before mapping: {}", searchResponse);
        List<MovieDTO> list = Arrays.stream(searchResponse.getHits().getHits())
                .map(hit -> mapToDTO(hit.getSourceAsMap(), hit.getId()))
                .collect(Collectors.toList());
        return new PageImpl(list);
    }

    private SearchResponse generateResponse(SearchRequest searchRequest) {
        Header[] header = {};

        try {
            SearchResponse searchResponse = elasticSearchClient.search(searchRequest, header);
            LOGGER.info("Search response: {}", searchResponse);
            return searchResponse;
        } catch (IOException e) {
            LOGGER.info("Failure: {}", e);
            e.printStackTrace();
        }
        return new SearchResponse();
    }

    public DocWriteResponse createOrUpdate(MovieDTO movieDTO) {
        LOGGER.info("About to createOrUpdate and index movie: {}", movieDTO);
        Movie movie = movieMapper.toDomain(movieDTO);

        if (indexAlreadyExists(movie)) {
            UpdateRequest updateRequest = new UpdateRequest("movies", "doc", movieDTO.getId())
                    .doc("title", movie.getTitle(),
                            "genre", movie.getGenre());

            try {
                UpdateResponse updateResponse = elasticSearchClient.update(updateRequest);
                LOGGER.info("Updated index: {}", updateResponse);
                return updateResponse;
            } catch (IOException e) {
                LOGGER.error("Failure: {}", e);
                e.printStackTrace();
            }
        }

        IndexRequest indexRequest = new IndexRequest("movies", "doc", movie.getId())
                .source("title", movie.getTitle(),
                        "genre", movie.getGenre());

        LOGGER.info("Before indexing: {}", indexRequest);
        try {
            IndexResponse indexResponse = elasticSearchClient.index(indexRequest);
            LOGGER.info("Created index: {}", indexResponse);
            return indexResponse;
        } catch (IOException e) {
            LOGGER.error("Failure: {}", e);
            e.printStackTrace();
        }

        return new IndexResponse();
    }

    private boolean indexAlreadyExists(Movie movie) {
        GetRequest getRequest = new GetRequest("movies", "doc", movie.getId());
        Header[] header = {};

        LOGGER.info("Before get request: {}", getRequest);
        try {
            GetResponse getResponse = elasticSearchClient.get(getRequest, header);

            if (CollectionUtils.isEmpty(getResponse.getSource())) {
                LOGGER.error("Index {} doesn't exist", movie.getId());
                return false;
            }
            LOGGER.info("Get response result: {}", getResponse);
            return movie.getId().equals(getResponse.getId());
        } catch (IOException e) {
            LOGGER.error("Failure: {}", e);
            e.printStackTrace();
        }
        return false;
    }

    private MovieDTO mapToDTO(Map<String, Object> sourceAsMap, String id) {
        ObjectMapper objectMapper = new ObjectMapper();
        MovieDTO movieDTO = objectMapper.convertValue(sourceAsMap, MovieDTO.class);
        movieDTO.setId(id);
        return movieDTO;
    }

    private SearchRequest extractSearchRequest(String field, String value) {
        SearchRequest searchRequest = new SearchRequest("movies");
        searchRequest.types("doc");

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(QueryBuilders.boolQuery()
                .should(QueryBuilders.queryStringQuery(value)
                        .lenient(true)
                        .field(field))
                .should(QueryBuilders.queryStringQuery("*"+value+"*")
                        .lenient(true)
                        .field(field)));

        sourceBuilder.from(0);
        sourceBuilder.size(50);
        sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
        return searchRequest.source(sourceBuilder);
    }

}
