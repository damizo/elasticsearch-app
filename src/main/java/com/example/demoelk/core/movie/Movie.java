package com.example.demoelk.core.movie;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

//it might contains list of dates or list of seances id

@Builder
@Data
@AllArgsConstructor
@Document(indexName = "movies", type = "doc", shards = 30)
public class Movie {

    @Id
    private String id;
    private String title;
    private MovieKind genre;

}
