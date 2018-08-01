package com.example.demoelk

import com.example.demoelk.common.dto.MovieDTO
import com.example.demoelk.common.mapper.IMovieCustomMapper
import com.example.demoelk.config.ElasticsearchConfiguration
import com.example.demoelk.core.movie.MovieKind
import com.example.demoelk.core.movie.MovieService
import com.fasterxml.jackson.databind.ObjectMapper
import org.elasticsearch.client.RestHighLevelClient
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

@SpringBootTest
@ContextConfiguration(classes = [ElasticsearchConfiguration.class])
class MovieSearchSpec extends Specification {

    @Autowired
    private IMovieCustomMapper movieCustomMapper

    @Autowired
    private RestHighLevelClient restHighLevelClient

    private MovieService movieService

    private ObjectMapper objectMapper

    private MovieDTO ingloriousBastardsMovie, snatchMovie, godfatherMovie, titanicMovie

    private static final Logger LOGGER = LoggerFactory.getLogger(MovieSearchSpec.class)

    def cleanup() {

    }

    def setup() {
        movieService = new MovieService(movieCustomMapper, restHighLevelClient)
        objectMapper = new ObjectMapper()
        initializeAndCreateIndexes()
    }

    def "should find a movies by titles"() {
        when: "user enters the title"
        def result = movieService.searchByTitle("tan")

        then: "1 movie should be searched"
        result.containsAll(titanicMovie)
    }

    def "should find a movie by genre"() {
        when: "user enters the genre"
        def result = movieService.searchByGenre("dram")

        then: "2 movies should be searched"
        result.containsAll(titanicMovie, godfatherMovie)
    }


    def "should find all movies "() {
        when: "user doesn't type filters"
        def result = movieService.searchAll()

        then: "4 movies should be searched"
        result.getContent().containsAll(titanicMovie, godfatherMovie, snatchMovie)
    }


    private void initializeAndCreateIndexes() {
       LOGGER.info("Initializing data...")

        ingloriousBastardsMovie = new MovieDTO(
                "1",
                "Inglorious Bastards",
                MovieKind.ACTION,
        )

        godfatherMovie = new MovieDTO(
                "2",
                "Godfather",
                MovieKind.DRAMA,
        )

        snatchMovie = new MovieDTO(
                "3",
                "Snatch",
                MovieKind.COMEDY,
        )

        titanicMovie = new MovieDTO(
                "4",
                "Titanic",
                MovieKind.DRAMA
        )

        movieService.createOrUpdate(ingloriousBastardsMovie)
        movieService.createOrUpdate(godfatherMovie)
        movieService.createOrUpdate(snatchMovie)
        movieService.createOrUpdate(titanicMovie)

    }


}

