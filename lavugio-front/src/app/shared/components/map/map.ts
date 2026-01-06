import { Component, AfterViewInit, signal } from '@angular/core';
import { environment } from 'environments/environment';
import * as L from 'leaflet';
import 'leaflet-routing-machine';
import { Coordinates } from '../../models/coordinates';
import { MarkerIcons } from './marker-icons';

@Component({
  selector: 'app-map',
  imports: [],
  templateUrl: './map.html',
  styleUrl: './map.css',
})
export class MapComponent implements AfterViewInit {
  private DefaultIcon: L.DivIcon = MarkerIcons.driverReserved;
  private routeControl: any;
  map: any;
  clickable = true;
  clickedLocation = signal<Coordinates | null>(null);


  ngAfterViewInit(): void {
    L.Marker.prototype.options.icon = this.DefaultIcon;
    this.initMap();
  }

  private initMap(): void {
    this.map = L.map('map', {
      center: [45.2396, 19.8227],
      zoom: 13,
      zoomControl: false
    });

    L.control.zoom({
      position: 'topright'
    }).addTo(this.map);

    const tiles = L.tileLayer(
      'https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png',
      {
        maxZoom: 18,
        minZoom: 3,
        attribution: '&copy; <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a>',
      }
    );
    tiles.addTo(this.map);

    if (this.clickable) {
      this.registerOnClick();
    }
  }

  private registerOnClick(): void {
    this.map.on('click', (e: any) => {
      const coords: Coordinates = {
        latitude: e.latlng.lat,
        longitude: e.latlng.lng
      };
      
      this.clickedLocation.set(coords);
      
      console.log('Kliknuto na:', coords);
    });
  }

  setRoute(coordinates: Coordinates[]): void {
    if (coordinates.length < 2) {
      console.error('Potrebne su minimum 2 koordinate');
      return;
    }

    if (this.routeControl) {
      this.map.removeControl(this.routeControl);
    }

    const waypoints = coordinates.map(coord => L.latLng(coord.latitude, coord.longitude));

    this.routeControl = (L.Routing.control as any)({
      waypoints: waypoints,
      router: L.routing.mapbox(environment.MAPBOX_API_KEY, { profile: 'mapbox/driving' }),
      addWaypoints: false,
      routeWhileDragging: false,
      fitSelectedRoutes: true,
      showAlternatives: false,
      createMarker: (i: number, waypoint: any) => {
        let icon;
        if (i === 0) {
          icon = MarkerIcons.start;
        } else if (i === coordinates.length - 1) {
          icon = MarkerIcons.end;
        } else {
          icon = MarkerIcons.checkpoint; // Za srednje tačke
        }
        return L.marker(waypoint.latLng, { icon });
      },
      lineOptions: {
        styles: [{ color: '#fc0505', weight: 3, opacity: 1 }],
        extendToWaypoints: true,
        missingRouteTolerance: 0
      }
    }).addTo(this.map);
    
    const container = this.routeControl.getContainer();
    if (container) {
      container.style.display = 'none';
    }
  }

  addMarker(location: Coordinates, icon: L.DivIcon): L.Marker {
    const marker = L.marker([location.latitude, location.longitude], { icon }).addTo(this.map);
    return marker;
  }

  addRemovableMarker(location: Coordinates, icon: L.DivIcon): L.Marker {
    const marker = L.marker([location.latitude, location.longitude], { icon }).addTo(this.map);
    marker.on('click', () => {
      this.removeMarker(marker);
    });
    return marker;
  }

  removeMarker(marker: L.Marker): void {
    this.map.removeLayer(marker);
  }

  resetMarkers(): void {
    this.map.eachLayer((layer: any) => {
      if (layer instanceof L.Marker) {
        this.map.removeLayer(layer);
      }
    });
  }

  ngOnDestroy(): void {
    if (this.map) {
      this.map.remove();
    }
  }

  removeRoute(): void {
    if (this.routeControl) {
      this.map.removeControl(this.routeControl);
      this.routeControl = undefined;
    }
  }
}