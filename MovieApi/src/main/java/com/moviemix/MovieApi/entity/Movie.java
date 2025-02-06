package com.moviemix.MovieApi.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer movieId;

    @Column(nullable = false, length = 200)
    @NotBlank(message = "title should not be null or empty. please provide movie title")
    private String title;

    @Column(nullable = false)
    @NotBlank(message = "title should not be null or empty. please provide movie director")
    private String director;

    @Column(nullable = false)
    @NotBlank(message = "title should not be null or empty. please provide movie studio")
    private String studio;

    @ElementCollection
    @CollectionTable(name = "movie_cast")
    private Set<String> movieCast;

    @Column(nullable = false)
    private Integer releaseYear;

    @Column(nullable = false)
    @NotBlank(message = "title should not be null or empty. please provide movie poster")
    private String poster;
}
