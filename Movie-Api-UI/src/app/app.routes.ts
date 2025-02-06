import { Routes } from '@angular/router';
import { HomeComponent } from './components/home/home.component';
import { LoginComponent } from './components/login/login.component';
import { RegisterComponent } from './components/register/register.component';
import { AddMovieComponent } from './components/add-movie/add-movie.component';
import { authGuard } from './guards/auth.guard';

export const routes: Routes = [
    {path:'', title:"MovieApp - Home", component:HomeComponent},
    {path:'login', title:"MovieApp - login", component:LoginComponent},
    {path:'register', title:"MovieApp - register", component:RegisterComponent},
    {path:'add-movie', title:"MovieApp - addMovie", component:AddMovieComponent, canActivate:[authGuard]},
];
