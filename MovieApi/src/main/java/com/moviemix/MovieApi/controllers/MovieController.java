package com.moviemix.MovieApi.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moviemix.MovieApi.Exception.EmptyFileException;
import com.moviemix.MovieApi.dto.MovieDTO;
import com.moviemix.MovieApi.dto.MoviePaginationRecordResponse;
import com.moviemix.MovieApi.service.MovieService;
import com.moviemix.MovieApi.util.AppConstants;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping(path="api/v1/movie")
@CrossOrigin(origins = "*")
public class MovieController {

    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping(path="add-movie")
    public ResponseEntity<MovieDTO> addMovieHandler( @RequestPart MultipartFile file, @RequestPart String movieDTO) throws IOException {
        if(file.isEmpty()){
            throw new EmptyFileException("File is Empty!.. Please send another file");
        }
    return new ResponseEntity<MovieDTO>(movieService.addMovie(convertStringToMovieDTO(movieDTO), file), HttpStatus.CREATED);
    }

    @GetMapping(path="get/{id}")
    public ResponseEntity<MovieDTO> getMovieIdHandler(@PathVariable("id") Integer movieId){
        return ResponseEntity.ok(movieService.getMovie(movieId));
    }


    @GetMapping(path="all")
    public ResponseEntity<List<MovieDTO>> getAllMovieHandler(){
        return ResponseEntity.ok(movieService.getAllMovies());
    }

    @GetMapping(path="allMoviesPages")
    public ResponseEntity<MoviePaginationRecordResponse> getMoviesWithPagination(
            @RequestParam(defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize

            ){

        return ResponseEntity.ok(movieService.getAllMoviesWithPagination(pageNumber, pageSize));
    }

    @GetMapping(path="allMoviesPagesSort")
    public ResponseEntity<MoviePaginationRecordResponse> getMoviesWithPaginationWothSort(
            @RequestParam(defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(defaultValue = AppConstants.SORT_BY, required = false) String sortBy,
            @RequestParam(defaultValue = AppConstants.SORT_DIR, required = false) String sortDir

    ){

        return ResponseEntity.ok(movieService.getAllMoviesWithPaginationAndSoring(pageNumber, pageSize, sortBy, sortDir));
    }

    @PutMapping(path="update/{id}")
    public ResponseEntity<MovieDTO> updateMovieHandler( @PathVariable("id")Integer id, @RequestPart String movieDTO, @RequestPart MultipartFile file) throws IOException {
        if(file.isEmpty()) file = null;
        return new ResponseEntity<MovieDTO>(movieService.updateMovieDTO(id,convertStringToMovieDTO(movieDTO), file), HttpStatus.OK);
    }

    @DeleteMapping(path="delete/{id}")
    public ResponseEntity<String> deleteMovieIdHandler(@PathVariable("id") Integer movieId) throws IOException {
        return ResponseEntity.ok(movieService.deleteMovie(movieId));
    }
    //method to convert String to JSON object
    private MovieDTO convertStringToMovieDTO(String movieDTOObj) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(movieDTOObj, MovieDTO.class);
    }
}
