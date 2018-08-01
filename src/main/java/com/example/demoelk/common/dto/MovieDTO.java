package com.example.demoelk.common.dto;

import com.example.demoelk.core.movie.MovieKind;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Created by damian on 30.07.18.
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MovieDTO {

    private String id;
    private String title;
    private MovieKind genre;

}