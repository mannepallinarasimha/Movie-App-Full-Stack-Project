import { HttpClient } from '@angular/common/http';
import { Injectable, WritableSignal, signal } from '@angular/core';
import { jwtDecode } from 'jwt-decode';
import { Observable, catchError, tap, throwError } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  public BASE_URL = "http://localhost:8081";
  private loggedIn = signal<boolean>(this.isAuthenticated());
  constructor(private http:HttpClient) { }

  register(registerRequest: RegisterRequest):Observable<AuthResponse>{
    return this.http.post<AuthResponse>(`${this.BASE_URL}/api/v1/auth/register`, registerRequest);
  }
  login(loginRequest: LoginRequest):Observable<AuthResponse>{
    return this.http.post<AuthResponse>(`${this.BASE_URL}/api/v1/auth/login`, loginRequest)
    .pipe(tap(response => {
      sessionStorage.setItem('accessToken', response.accessToken);
      sessionStorage.setItem('refreshToken', response.refreshToken);
      sessionStorage.setItem('name', response.name);
      sessionStorage.setItem('email', response.email);
      sessionStorage.setItem('username', response.username);

      const decodeToken: any = jwtDecode(response.accessToken);
      console.log("decodeToken: "+decodeToken);
      sessionStorage.setItem('role', decodeToken.role[0].authority);
    }));
  }
  logout():void{
    sessionStorage.removeItem('accessToken');
    sessionStorage.removeItem('refreshToken');
    sessionStorage.removeItem('name');
    sessionStorage.removeItem('email');
    sessionStorage.removeItem('username');
  }
  setLoggedIn(value: boolean){
    this.loggedIn.set(value);
  }
  getLoggedIn(): WritableSignal<boolean>{
    return this.loggedIn;
  }
  isAuthenticated(): boolean {
    const token = sessionStorage.getItem('accessToken');
    return token != null && !this.isTokenExpired(token);
  }

  isTokenExpired(token: string): boolean{
    const decodedToken: any = jwtDecode(token);
    return (decodedToken.exp * 1000) < Date.now();
  }
  refreshToken():Observable<any>{
    const refreshToken = sessionStorage.getItem('refreshToken');
    const rTObj : RefreshTokenRequest = {
      refreshToken: refreshToken
    }
    return this.http.post(`$${this.BASE_URL}/api/v1/auth/refresh`, {rTObj}).pipe(
      tap((res: any) => {
        sessionStorage.setItem('accessToken', res.accessToken);
      }),
      catchError(error => {
        this.logout();
        return throwError(() => error);
      })
    )
  }
  hasRole(role: string): boolean{
    const token  = sessionStorage.getItem('accessToken');
    if(token){
      const decodeToken: any = jwtDecode(token); 
      return decodeToken?.role[0]?.authority.includes(role);
    }
    return false;
  }
}
export type RegisterRequest = {
    name: string,
    email: string,
    username: string,
    password: string
}
export type LoginRequest = {
  email: string,
  password: string
}
export type AuthResponse = {
  accessToken: string,
  refreshToken: string,
  name: string,
  email: string,
  username: string
}
export type RefreshTokenRequest = {
  refreshToken: string | null;
}
