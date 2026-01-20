import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { NewFavoriteRouteRequest } from '@app/shared/models/route/newFavoriteRouteRequest';
import { environment } from 'environments/environment';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class FavoriteRouteService {
  private apiUrl = environment.BACKEND_URL;

  constructor(private http: HttpClient) {}

  saveFavoriteRoute(favoriteRoute: NewFavoriteRouteRequest): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/favorite-routes/add`, favoriteRoute);
  }

  getFavoriteRoutes(): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/favorite-routes`);
  }
}
