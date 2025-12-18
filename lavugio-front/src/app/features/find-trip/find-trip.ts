import { Component } from '@angular/core';
import { FormBackgroundSheet } from '@app/layout/form-background-sheet/form-background-sheet';
import { Navbar } from '@app/layout/navbar/navbar';


@Component({
  selector: 'app-find-trip',
  imports: [Navbar, FormBackgroundSheet],
  templateUrl: './find-trip.html',
  styleUrl: './find-trip.css',
})
export class FindTrip {
  isPanelOpen: boolean = false;

  togglePanel() {
    this.isPanelOpen = !this.isPanelOpen;
  }
}
