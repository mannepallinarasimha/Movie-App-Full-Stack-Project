package com.moviemix.MovieApi.dto;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovieDTO {
    private Integer movieId;

    @NotBlank(message = "title should not be null or empty. please provide movie title")
    private String title;

    @NotBlank(message = "title should not be null or empty. please provide movie director")
    private String director;

    @NotBlank(message = "title should not be null or empty. please provide movie studio")
    private String studio;

    private Set<String> movieCast;

    private Integer releaseYear;

    @NotBlank(message = "title should not be null or empty. please provide movie poster")
    private String poster;

    @NotBlank(message = "title should not be null or empty. please provide movie poster URL")
    private String posterUrl;
}
