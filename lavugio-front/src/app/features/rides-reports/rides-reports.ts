import { Component, inject, signal } from '@angular/core';
import { Navbar } from '@app/shared/components/navbar/navbar';
import { WhiteSheetBackground } from '@app/shared/components/white-sheet-background/white-sheet-background';
import { DateFilter } from '../trip-history-driver/date-filter/date-filter';
import { AuthService } from '@app/core/services/auth-service';

@Component({
  selector: 'app-rides-reports',
  imports: [Navbar, WhiteSheetBackground, DateFilter],
  templateUrl: './rides-reports.html',
  styleUrl: './rides-reports.css',
})
export class RidesReports {
  authService = inject(AuthService);
  startDate = signal<string>('');
  endDate = signal<string>('');

  updateSelectedDate(event: { startDate: string; endDate: string }) {
    this.startDate.set(event.startDate);
    this.endDate.set(event.endDate);
    console.log(`Date range selected: ${event.startDate} to ${event.endDate}`);
  }

  loadReportsForRole() {
    if (this.authService.isAdmin()) {
      this.loadAdminReports();
      return;
    }

    if (this.authService.isDriver()) {
      this.loadDriverReports();
      return;
    }

    if (this.authService.isRegularUser()) {
      this.loadUserReports();
    }
  }

  private loadAdminReports() { /* ... */ }
  private loadDriverReports() { /* ... */ }
  private loadUserReports() { /* ... */ }
}
