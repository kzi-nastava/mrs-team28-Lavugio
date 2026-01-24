import { Component, inject, signal, Signal, WritableSignal } from '@angular/core';
import { Navbar } from '@app/shared/components/navbar/navbar';
import { DriverUpdateRequest } from './components/driver-update-request/driver-update-request';
import { UserProfile } from '@app/shared/models/user/userProfile';
import { DriverService } from '@app/core/services/user/driver-service';
import { DriverUpdateRequestDiffDTO } from '@app/shared/models/user/editProfileDTO';

interface DriverUpdateRequestData {
  oldProfile: UserProfile;
  newProfile: UserProfile;
  editRequestId: number;
}

@Component({
  selector: 'app-driver-update-requests',
  imports: [Navbar, DriverUpdateRequest],
  templateUrl: './driver-update-requests.html',
  styleUrl: './driver-update-requests.css',
})
export class DriverUpdateRequests {
  
  // Hardcoded test data for driver update requests
  updateRequests: WritableSignal<DriverUpdateRequestDiffDTO[]> = signal<DriverUpdateRequestDiffDTO[]>([]);
  
  driverService: DriverService = inject(DriverService);

  ngOnInit() {
    this.driverService.getEditRequests().subscribe({
      next: (requests) => {
        console.log(requests);
        this.updateRequests.set(requests);
      },
      error: (err) => {
        console.error("Failed to load driver update requests:", err);
      }
    });
  }

  handleApprove(requestId: number) {
    this.driverService.approveEditRequest(requestId).subscribe({
      next: () => {
        this.updateRequests.update((requests) => requests.filter((r) => r.requestId !== requestId));
      },
      error: (err) => {
        console.error('Failed to approve driver update request:', err);
      },
    });
  }

  handleReject(requestId: number) {
    this.driverService.rejectEditRequest(requestId).subscribe({
      next: () => {
        this.updateRequests.update((requests) => requests.filter((r) => r.requestId !== requestId));
      },
      error: (err) => {
        console.error('Failed to reject driver update request:', err);
      },
    });
  }

  
}
