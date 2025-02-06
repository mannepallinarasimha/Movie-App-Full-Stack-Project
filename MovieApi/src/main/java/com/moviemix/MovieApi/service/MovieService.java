package com.moviemix.MovieApi.service;

import com.moviemix.MovieApi.dto.MovieDTO;
import com.moviemix.MovieApi.dto.MoviePaginationRecordResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface MovieService {
    //add movie to DB
    public MovieDTO addMovie(MovieDTO movieDTO, MultipartFile file)throws IOException;
    //fetch Movie from DB
    public MovieDTO getMovie(Integer MovieId);

    //fetch Movie LIST from DB
    public List<MovieDTO> getAllMovies();

    //update Movie
    public MovieDTO updateMovieDTO(Integer movieId, MovieDTO movieDTO, MultipartFile file) throws IOException;

    //delete movie by ID
   public String deleteMovie(Integer movieId) throws IOException;

   //Movies list with pagination
   MoviePaginationRecordResponse getAllMoviesWithPagination(Integer pageNumber, Integer pageSize);

    MoviePaginationRecordResponse getAllMoviesWithPaginationAndSoring(Integer pageNumber, Integer pageSize, String sortBy, String dir);
}
