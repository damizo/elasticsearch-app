package com.example.demoelk.web.movie;

import com.example.demoelk.common.dto.MovieDTO;
import com.example.demoelk.core.movie.Movie;
import com.example.demoelk.core.movie.MovieService;
import org.elasticsearch.action.search.SearchResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/movies/search/")
public class MovieSearchResource {

    @Autowired
    private MovieService movieService;


    @GetMapping("/title/{title}")
    public Page<MovieDTO> searchByTitle(final @PathVariable String title) {
        return movieService.searchByTitle(title);
    }

    @GetMapping("/genre/{genre}")
    public Page<MovieDTO> searchByGenre(final @PathVariable String genre) {
        return movieService.searchByGenre(genre);
    }

    @GetMapping("/all")
    public Page<MovieDTO> searchAll() {
        return movieService.searchAll();
    }

}
