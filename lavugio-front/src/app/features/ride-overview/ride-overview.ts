import { Component } from '@angular/core';
import { Navbar } from '@app/shared/components/navbar/navbar';
import { MapComponent } from '@app/shared/components/map/map';
import { RideInfo } from './ride-info/ride-info';
import { signal } from '@angular/core';

@Component({
  selector: 'app-ride-overview',
  imports: [Navbar, MapComponent, RideInfo],
  templateUrl: './ride-overview.html',
  
  styleUrl: './ride-overview.css',
})
export class RideOverview {
  isInfoOpen = signal(false);

  toggleInfo() {
    this.isInfoOpen.update(v => !v);
  }

  closeInfo() {
    this.isInfoOpen.set(false);
  }

  openInfo() {
    this.isInfoOpen.set(true);
  }
}
