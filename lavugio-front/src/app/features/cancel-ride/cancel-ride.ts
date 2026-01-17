import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { DialogService } from '@app/core/services/dialog-service';
import { Navbar } from '@app/shared/components/navbar/navbar';

@Component({
  selector: 'app-cancel-ride',
  standalone: true,
  imports: [FormsModule, CommonModule, Navbar],
  templateUrl: './cancel-ride.html',
  styleUrl: './cancel-ride.css',
})
export class CancelRide {
  dialogService = inject(DialogService);
  reason = signal('');
  isLoading = signal(false);
  error = signal('');

  submitCancellation() {
    if (!this.reason().trim()) {
      this.dialogService.open('Error', 'You must provide a reason for cancellation.', true);
      return;
    }
    this.isLoading.set(true);
    // TODO: Pozvati RideService za otkazivanje vožnje
    setTimeout(() => {
      this.isLoading.set(false);
      this.dialogService.open('Success', 'The ride has been successfully cancelled.', false);
      // TODO: Navigacija nazad na pregled vožnji ili profil
    }, 1200);
  }
}
