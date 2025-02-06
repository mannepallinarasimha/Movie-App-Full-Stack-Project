package com.moviemix.MovieApi.service.impl;

import com.moviemix.MovieApi.Exception.FileExistsException;
import com.moviemix.MovieApi.Exception.MovieNotFoundException;
import com.moviemix.MovieApi.dto.MovieDTO;
import com.moviemix.MovieApi.dto.MoviePaginationRecordResponse;
import com.moviemix.MovieApi.entity.Movie;
import com.moviemix.MovieApi.repository.MovieRepository;
import com.moviemix.MovieApi.service.FileService;
import com.moviemix.MovieApi.service.MovieService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
public class MovieServiceImpl implements MovieService {

    private final MovieRepository movieRepository;

    public MovieServiceImpl(MovieRepository movieRepository, FileService fileService) {
        this.movieRepository = movieRepository;
        this.fileService = fileService;
    }

    private final FileService fileService;

    @Value("${project.poster}")
    private String path;

    @Value("${base.url}")
    private String baseUrl;

    @Override
    public MovieDTO addMovie(MovieDTO movieDTO, MultipartFile file) throws IOException {

        if(Files.exists(Paths.get(path+ File.separator+file.getOriginalFilename()))){
            throw new FileExistsException("File name already exist please enter another file name.");
        }
        //1. to upload the file
        String uploadedFileName = fileService.uploadFile(path, file);
        System.out.println("uploadedFileName : "+uploadedFileName);

        //2. to set the value of field 'poster' as filename
        movieDTO.setPoster(uploadedFileName);

        //3.  map DTO to Movie object
        Movie movie  = new Movie(
                null,
                movieDTO.getTitle(),
                movieDTO.getDirector(),
                movieDTO.getStudio(),
                movieDTO.getMovieCast(),
                movieDTO.getReleaseYear(),
                movieDTO.getPoster()
        );

        //4. Save the movie object
        Movie savedMovie = movieRepository.save(movie);

        // 5. generate the posterURL
        String posterUrl = baseUrl + "/file/" +uploadedFileName;

        // 6. map movie obj to DTO object and return
        return new MovieDTO(
                savedMovie.getMovieId(),
                savedMovie.getTitle(),
                savedMovie.getDirector(),
                savedMovie.getStudio(),
                savedMovie.getMovieCast(),
                savedMovie.getReleaseYear(),
                savedMovie.getPoster(),
                posterUrl
        );
    }

    @Override
    public MovieDTO getMovie(Integer movieId) {
        // check the data in DB and if exist fetch the data of given ID
        Movie movie = movieRepository.findById(movieId).orElseThrow(() -> new MovieNotFoundException("Movie Not found with %d ID : " + movieId));

        // we need to generate poster URL
        String posterUrl = baseUrl +"/file/" +movie.getPoster();
        System.out.println("posterUrl : "+posterUrl);
        //map to MovieDTO object and return

        return new MovieDTO(
                movie.getMovieId(),
                movie.getTitle(),
                movie.getDirector(),
                movie.getStudio(),
                movie.getMovieCast(),
                movie.getReleaseYear(),
                movie.getPoster(),
                posterUrl
        );
    }

    @Override
    public List<MovieDTO> getAllMovies() {
        //fetch all data from DB
        List<Movie> allMovies = movieRepository.findAll();
        List<MovieDTO> movieDTOs = new ArrayList<>();

        // iterate through list and then generate posterUrl each movie obj and map to MOvieDTO object
        for(Movie movie : allMovies){
            String posterUrl = baseUrl +"/file/" +movie.getPoster();
            System.out.println("posterUrl : "+posterUrl);
            MovieDTO response = new MovieDTO(
                    movie.getMovieId(),
                    movie.getTitle(),
                    movie.getDirector(),
                    movie.getStudio(),
                    movie.getMovieCast(),
                    movie.getReleaseYear(),
                    movie.getPoster(),
                    posterUrl
            );
            movieDTOs.add(response);
        }
        return movieDTOs;
    }

    public MovieDTO updateMovieDTO(Integer movieId, MovieDTO movieDTO, MultipartFile file) throws IOException {
        //get movie by ID
        Movie mv = movieRepository.findById(movieId).orElseThrow(() -> new MovieNotFoundException("Movie Not found with %d ID : " + movieId));
        String fileName = mv.getPoster();
        if(file != null){
            //delete existing
            Files.deleteIfExists(Paths.get(path+File.separator+fileName));
            fileName = fileService.uploadFile(path, file);
        }
        movieDTO.setPoster(fileName);
        Movie movie = new Movie(
                mv.getMovieId(),
                movieDTO.getTitle(),
                movieDTO.getDirector(),
                movieDTO.getStudio(),
                movieDTO.getMovieCast(),
                movieDTO.getReleaseYear(),
                movieDTO.getPoster()
        );
        Movie updatedMovie = movieRepository.save(movie);

        String posterUrl = baseUrl +"/file/" +fileName;

        return new MovieDTO(
                updatedMovie.getMovieId(),
                updatedMovie.getTitle(),
                updatedMovie.getDirector(),
                updatedMovie.getStudio(),
                updatedMovie.getMovieCast(),
                updatedMovie.getReleaseYear(),
                updatedMovie.getPoster(),
                posterUrl
        );
    }

    @Override
    public String deleteMovie(Integer movieId) throws IOException {
        Movie mv = movieRepository.findById(movieId).orElseThrow(() -> new MovieNotFoundException("Movie Not found with %d ID : " + movieId));
        Files.deleteIfExists(Paths.get(path+File.separator+mv.getPoster()));
        movieRepository.deleteById(mv.getMovieId());
        return "Deleted Successfully with id %d..."+mv.getMovieId();
    }

//    @Override
//    public MoviePaginationRecordResponse getAllMoviesWithPagination(Integer pageNumber, Integer pageSize) {
//        Pageable pageable = PageRequest.of(pageNumber, pageSize);
//        Page<Movie> moviePages = movieRepository.findAll(pageable);
//        List<Movie> allMovies = moviePages.getContent();
//        // iterate through list and then generate posterUrl each movie obj and map to MOvieDTO object
//        List<MovieDTO> movieDTOS = new ArrayList<>();
//        for(Movie movie : allMovies){
//            String posterUrl = baseUrl +"/file/" +movie.getPoster();
//            System.out.println("posterUrl : "+posterUrl);
//            MovieDTO response = new MovieDTO(
//                    movie.getMovieId(),
//                    movie.getTitle(),
//                    movie.getDirector(),
//                    movie.getStudio(),
//                    movie.getMovieCast(),
//                    movie.getReleaseYear(),
//                    movie.getPoster(),
//                    posterUrl
//            );
//            movieDTOS.add(response);
//        }
//        return new MoviePaginationRecordResponse(
//                movieDTOS,pageNumber, pageSize,moviePages.getNumberOfElements(), moviePages.getTotalPages(), moviePages.isLast()
//        );
//    }
//
//    @Override
//    public MoviePaginationRecordResponse getAllMoviesWithPaginationAndSoring(Integer pageNumber, Integer pageSize, String sortBy, String dir) {
//        Sort sort = dir.equalsIgnoreCase("asc")?Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
//        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
//        Page<Movie> moviePages = movieRepository.findAll(pageable);
//        List<Movie> allMovies = moviePages.getContent();
//        // iterate through list and then generate posterUrl each movie obj and map to MovieDTO object
//        List<MovieDTO> movieDTOS = new ArrayList<>();
//        for(Movie movie : allMovies){
//            String posterUrl = baseUrl +"/file/" +movie.getPoster();
//            System.out.println("posterUrl : "+posterUrl);
//            MovieDTO response = new MovieDTO(
//                    movie.getMovieId(),
//                    movie.getTitle(),
//                    movie.getDirector(),
//                    movie.getStudio(),
//                    movie.getMovieCast(),
//                    movie.getReleaseYear(),
//                    movie.getPoster(),
//                    posterUrl
//            );
//            movieDTOS.add(response);
//        }
//        return new MoviePaginationRecordResponse(
//                movieDTOS,pageNumber, pageSize,moviePages.getNumberOfElements(), moviePages.getTotalPages(), moviePages.isLast()
//        );
//    }

    @Override
    public MoviePaginationRecordResponse getAllMoviesWithPagination(Integer pageNumber, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        Page<Movie> moviePages = movieRepository.findAll(pageable);
        List<Movie> movies = moviePages.getContent();

        List<MovieDTO> movieDtos = new ArrayList<>();

        // 2. iterate through the list, generate posterUrl for each movie obj,
        // and map to MovieDto obj
        for(Movie movie : movies) {
            String posterUrl = baseUrl + "/file/" + movie.getPoster();
            MovieDTO movieDto = new MovieDTO(
                    movie.getMovieId(),
                    movie.getTitle(),
                    movie.getDirector(),
                    movie.getStudio(),
                    movie.getMovieCast(),
                    movie.getReleaseYear(),
                    movie.getPoster(),
                    posterUrl
            );
            movieDtos.add(movieDto);
        }


        return new MoviePaginationRecordResponse(movieDtos, pageNumber, pageSize,
                moviePages.getTotalElements(),
                moviePages.getTotalPages(),
                moviePages.isLast());
    }

    @Override
    public MoviePaginationRecordResponse getAllMoviesWithPaginationAndSoring(Integer pageNumber, Integer pageSize,
                                                                  String sortBy, String dir) {
        Sort sort = dir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

        Page<Movie> moviePages = movieRepository.findAll(pageable);
        List<Movie> movies = moviePages.getContent();

        List<MovieDTO> movieDtos = new ArrayList<>();

        // 2. iterate through the list, generate posterUrl for each movie obj,
        // and map to MovieDto obj
        for(Movie movie : movies) {
            String posterUrl = baseUrl + "/file/" + movie.getPoster();
            MovieDTO movieDto = new MovieDTO(
                    movie.getMovieId(),
                    movie.getTitle(),
                    movie.getDirector(),
                    movie.getStudio(),
                    movie.getMovieCast(),
                    movie.getReleaseYear(),
                    movie.getPoster(),
                    posterUrl
            );
            movieDtos.add(movieDto);
        }


        return new MoviePaginationRecordResponse(movieDtos, pageNumber, pageSize,
                moviePages.getTotalElements(),
                moviePages.getTotalPages(),
                moviePages.isLast());
    }

}
