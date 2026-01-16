import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'environments/environment';

@Injectable({
  providedIn: 'root',
})
export class MapService {
  private http = inject(HttpClient);
  constructor() {}

  search(street: string): Observable<any> {
    return this.http.get(
      'https://nominatim.openstreetmap.org/search?format=json&q=' + street
    );
  }

  reverseSearch(lat: number, lon: number): Observable<any> {
    return this.http.get(
      `https://nominatim.openstreetmap.org/reverse?format=json&lat=${lat}&lon=${lon}&<params>`
    );
  }

  getRoute(waypoints: { lat: number; lon: number }[]): Observable<any> {
    if (waypoints.length < 2) throw new Error('Two or more waypoints required');

    const coords = waypoints.map(p => `${p.lon},${p.lat}`).join(';');
    const url = `https://api.mapbox.com/directions/v5/mapbox/driving/${coords}?geometries=geojson&access_token=${environment.MAPBOX_API_KEY}`;

    return this.http.get(url);
  }
}
