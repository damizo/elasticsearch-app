package com.example.demoelk.common.mapper;

import com.example.demoelk.common.dto.MovieDTO;
import com.example.demoelk.core.movie.Movie;
import org.springframework.stereotype.Component;

/**
 * Created by damian on 30.07.18.
 */
public class MovieCustomMapper implements IMovieCustomMapper{

    @Override
    public MovieDTO toDTO(Movie movie) {
        return MovieDTO.builder()
                .id(movie.getId())
                .genre(movie.getGenre())
                .title(movie.getTitle())
                .build();
    }

    @Override
    public Movie toDomain(MovieDTO movie) {
        return Movie.builder()
                .id(movie.getId())
                .genre(movie.getGenre())
                .title(movie.getTitle())
                .build();
    }
}
