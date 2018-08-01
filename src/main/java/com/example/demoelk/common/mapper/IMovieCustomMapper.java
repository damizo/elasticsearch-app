package com.example.demoelk.common.mapper;

import com.example.demoelk.common.dto.MovieDTO;
import com.example.demoelk.core.movie.Movie;
import org.springframework.stereotype.Component;

/**
 * Created by damian on 30.07.18.
 */

@Component
public interface IMovieCustomMapper {

    MovieDTO toDTO(Movie movie);

    Movie toDomain(MovieDTO movie);
}

