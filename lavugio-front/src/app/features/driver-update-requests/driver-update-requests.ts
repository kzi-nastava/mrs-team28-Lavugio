import { Component } from '@angular/core';
import { Navbar } from '@app/shared/components/navbar/navbar';
import { DriverUpdateRequest } from './components/driver-update-request/driver-update-request';

@Component({
  selector: 'app-driver-update-requests',
  imports: [Navbar, DriverUpdateRequest],
  templateUrl: './driver-update-requests.html',
  styleUrl: './driver-update-requests.css',
})
export class DriverUpdateRequests {

}
