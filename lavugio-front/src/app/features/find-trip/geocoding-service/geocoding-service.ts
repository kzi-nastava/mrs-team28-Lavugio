import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError, map, Observable, of } from 'rxjs';
import { Coordinates } from '@app/shared/models/coordinates';
import { RouteEstimateInfo } from '@app/shared/models/route/routeEstimateInfo';

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

export interface NominatimReverseResponse {
  display_name: string;
  lat: string;
  lon: string;
  address: {
    road?: string;
    house_number?: string;
    suburb?: string;
    neighbourhood?: string;
    city?: string;
    town?: string;
    village?: string;
    municipality?: string;
    postcode?: string;
    country?: string;
  };
}

@Injectable({
  providedIn: 'root',
})
export class GeocodingService {
  // Photon - a free geocoding API with CORS support
  private photonUrl = 'https://photon.komoot.io/api/';
  private photonReverseUrl = 'https://photon.komoot.io/reverse';

  // OSRM - Open Source Routing Machine for route estimation
  private osrmUrl = 'https://router.project-osrm.org/route/v1/driving/';

  private reverseNominatimApiUrl = 'https://nominatim.openstreetmap.org/reverse';

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

  reverseGeocode(lat: number, lon: number): Observable<GeocodeResult | null> {
    const params = {
      lat: lat.toString(),
      lon: lon.toString(),
      limit: '5',
      radius: '50', // search within 50 meters for better accuracy
    };

    return this.http.get<PhotonResponse>(this.photonReverseUrl, { params }).pipe(
      map((response) => {
        if (!response.features?.length) return null;

        const results = this.transformPhotonResults(response);

        // Prioritize results with both street and number, then street only, then any result

        return (
          results.find((r) => r.street && r.streetNumber) ||
          results.find((r) => r.street) ||
          results[0]
        );
      }),
      catchError(() => of(null)),
    );
  }

  reverseGeocodeNominatim(lat: number, lon: number): Observable<GeocodeResult | null> {
    const params = {
      format: 'jsonv2',
      lat: lat.toString(),
      lon: lon.toString(),
      zoom: '18', // 18 = building level
      addressdetails: '1',
    };

    return this.http
      .get<NominatimReverseResponse>(this.reverseNominatimApiUrl, { params })
      .pipe(
        map((res) => {
          if (!res || !res.address) return null;

          const a = res.address;

          const city = a.city || a.town || a.village || a.municipality || '';

          let displayName = '';

          if (a.road) {
            displayName = a.road;

            if (a.house_number) {
              displayName += ' ' + a.house_number;
            }

            if (city) {
              displayName += ', ' + city;
            }
          } else if (city) {
            displayName = city;
          } else {
            displayName = res.display_name;
          }

          return {
            place_id: 0,
            display_name: displayName,
            lat: res.lat,
            lon: res.lon,
            type: 'reverse',
            street: a.road,
            streetNumber: a.house_number ?? null,
            city: city,
            country: a.country,
          };
        }),
        catchError((err) => {
          console.error('Nominatim reverse error:', err);
          return of(null);
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

  /**
   * Return distance in meters and duration in seconds for provided array
   * of coordinates
   * @param coordinates - array of Coordinate class
   * @returns RouteEstimateInfo
   */
  getRouteInfo(coordinates: Array<Coordinates>): Observable<RouteEstimateInfo | null> {
    if (!coordinates || coordinates.length < 2) {
      return of(null);
    }

    // Format for OSRM: lon,lat;lon,lat;lon,lat;lon,lat;...
    const coordString = coordinates
      .map((coord) => `${coord.longitude},${coord.latitude}`)
      .join(';');

    const urlToHit = `${this.osrmUrl}${coordString}`;

    const params = {
      overview: 'full',
      geometries: 'geojson',
    };

    return this.http.get<any>(urlToHit, { params }).pipe(
      map((response) => {
        if (!response.routes || response.routes.lenght === 0) {
          return null;
        }

        const route = response.routes[0];
        return {
          distanceMeters: route.distance,
          durationSeconds: route.duration,
        };
      }),
      catchError((error) => {
        console.error('Routing error:', error);
        return of(null);
      }),
    );
  }

  /**
   * Helper to format route info into user-friendly strings
   * @param info - RouteEstimateInfo object
   * @returns Object with distance in km and duration in min as strings
   */
  formatRouteInfo(info: RouteEstimateInfo): { distanceKm: string; durationMin: string } {
    return {
      distanceKm: (info.distanceMeters / 1000).toFixed(2),
      durationMin: Math.ceil(info.durationSeconds / 60).toString(),
    };
  }
}
