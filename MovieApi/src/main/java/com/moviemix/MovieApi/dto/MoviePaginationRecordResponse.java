package com.moviemix.MovieApi.dto;

import java.util.List;

public record MoviePaginationRecordResponse(List<MovieDTO> movieDTOs,
                                    Integer pageNumber,
                                    Integer pageSize,
                                    long totalElements,
                                    int totalPages,
                                    boolean isLast){
}
