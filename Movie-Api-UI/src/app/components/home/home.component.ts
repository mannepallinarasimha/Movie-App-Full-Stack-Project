import { Component, OnInit, inject } from '@angular/core';
import { MovieDTO, MovieService } from '../../services/movie.service';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent implements OnInit{

  movies : MovieDTO[] = [];
  movieService = inject(MovieService);
  ngOnInit(): void {
    this.getAllMoviesList();
  }
  getAllMoviesList(){
    this.movieService.getAllMovies().subscribe({
      next: (res: any) =>{
        // this.movies = res;
        console.log(res);

      },
      error: (error: any)=>{
        console.log(error);

      }
    })
  }
  
}
