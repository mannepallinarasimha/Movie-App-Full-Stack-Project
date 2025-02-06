import { HttpClient } from "@angular/common/http";
import { Injectable, inject } from "@angular/core";
import { Observable } from "rxjs";

@Injectable({
    providedIn: 'root'
  })

export class MovieService{
    public BASE_URL = "http://localhost:8081";
    http = inject(HttpClient);
    // getAllMovies(): Observable<MovieDto[]>{
    //     return this.http.get<MovieDto[]>(`${this.BASE_URL}/api/v1/movie/all`);
    // }
    getAllMovies(): Observable<MovieDTO[]> {
        return this.http.get<MovieDTO[]>(`${this.BASE_URL}/api/v1/movie/all`);
    }
}  
export type MovieDTO = {
    movieId?: number,
    title: string,
    director: string,
    studio: string,
    movieCast: string[],
    releaseYear: number,
    poster? : string,
    posterUrl?: string
}  