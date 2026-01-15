import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError, map, Observable, of } from 'rxjs';

export interface GeocodeResult {
  place_id?: number;
  display_name: string;
  lat: string;
  lon: string;
  type: string;
  address?: {
    city?: string;
    country?: string;
    state?: string;
  };
}

interface PhotonFeature {
  properties: {
    osm_id: number;
    name: string;
    country?: string;
    city?: string;
    state?: string;
    street?: string;
    housenumber?: string;
    postcode?: string;
    type: string;
  };
  geometry: {
    coordinates: [number, number];
  };
}

interface PhotonResponse {
  features: PhotonFeature[];
}

@Injectable({
  providedIn: 'root'
})
export class GeocodingService {
  // Photon - a free geocoding API with CORS support
  private photonUrl = 'https://photon.komoot.io/api/';

  constructor(private http: HttpClient) {}

  searchLocations(query: string): Observable<GeocodeResult[]> {
    if (!query || query.length < 3) {
      return of([]);
    }

    const params = {
      q: query,
      limit: '5'
    };

    return this.http.get<PhotonResponse>(this.photonUrl, { params }).pipe(
      map(response => this.transformPhotonResults(response)),
      catchError((error) => {
        console.error('Geocoding error:', error);
        return of([]);
      })
    );
  }

  private transformPhotonResults(response: PhotonResponse): GeocodeResult[] {
    return response.features.map(feature => {
      const props = feature.properties;
      const displayParts = [
        props.name,
        props.street,
        props.housenumber,
        props.city,
        props.state,
        props.country
      ].filter(Boolean);

      return {
        place_id: props.osm_id,
        display_name: displayParts.join(', '),
        lat: feature.geometry.coordinates[1].toString(),
        lon: feature.geometry.coordinates[0].toString(),
        type: props.type,
        address: {
          city: props.city,
          country: props.country,
          state: props.state
        }
      };
    });
  }
}
