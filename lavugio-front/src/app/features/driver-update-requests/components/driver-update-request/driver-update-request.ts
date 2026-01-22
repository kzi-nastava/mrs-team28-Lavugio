import { Component, signal } from '@angular/core';
import { DriverUpdateRequestRow } from '../driver-update-request-row/driver-update-request-row';

@Component({
  selector: 'app-driver-update-request',
  imports: [DriverUpdateRequestRow],
  templateUrl: './driver-update-request.html',
  styleUrl: './driver-update-request.css',
})
export class DriverUpdateRequest {

  isExpanded = signal<boolean>(false);

  toggleExpand() {
    this.isExpanded.set(!this.isExpanded());
  }
}
