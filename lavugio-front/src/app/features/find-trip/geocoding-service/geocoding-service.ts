import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError, map, Observable, of } from 'rxjs';

export interface GeocodeResult {
  place_id?: number;
  display_name: string;

  lat: string;
  lon: string;

  type: string;

  street?: string;
  streetNumber?: string | null;
  city?: string;
  country?: string;
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
  providedIn: 'root',
})
export class GeocodingService {
  // Photon - a free geocoding API with CORS support
  private photonUrl = 'https://photon.komoot.io/api/';
  private photonReverseUrl = 'https://photon.komoot.io/reverse';

  constructor(private http: HttpClient) {}

  searchLocations(query: string): Observable<GeocodeResult[]> {
    if (!query || query.length < 3) {
      return of([]);
    }

    const params = {
      q: query,
      limit: '5',
    };

    return this.http.get<PhotonResponse>(this.photonUrl, { params }).pipe(
      map((response) => this.transformPhotonResults(response)),
      catchError((error) => {
        console.error('Geocoding error:', error);
        return of([]);
      }),
    );
  }

  private transformPhotonResults(response: PhotonResponse): GeocodeResult[] {
    return response.features.map((feature) => {
      const props = feature.properties;

      let displayName = '';

      if (props.street) {
        displayName = props.street;

        if (props.housenumber) {
          displayName += ' ' + props.housenumber;
        }

        if (props.city) {
          displayName += ', ' + props.city;
        }
      } else if (props.city) {
        displayName = props.city;
      } else {
        displayName = props.name || 'Unknown location';
      }

      return {
        place_id: props.osm_id,

        display_name: displayName,

        lat: feature.geometry.coordinates[1].toString(),
        lon: feature.geometry.coordinates[0].toString(),

        type: props.type,

        street: props.street,
        streetNumber: props.housenumber ?? null,
        city: props.city,
        country: props.country,
      };
    });
  }

  reverseGeocode(lat: number, lon: number): Observable<GeocodeResult | null> {
    const params = {
      lat: lat.toString(),
      lon: lon.toString(),
      limit: '1',
    };

    return this.http.get<PhotonResponse>(this.photonReverseUrl, { params }).pipe(
      map((response) => {
        if (!response.features || response.features.length === 0) {
          return null;
        }

        const result = this.transformPhotonResults(response)[0];

        if (!result.display_name || result.display_name.trim() === '') {
          result.display_name = `Location (${lat.toFixed(5)}, ${lon.toFixed(5)})`;
        }

        return result;
      }),
      catchError((error) => {
        console.error('Reverse geocoding error:', error);
        return of(null);
      }),
    );
  }
}
