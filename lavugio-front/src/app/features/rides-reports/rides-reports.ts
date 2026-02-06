import { Component, inject, signal } from '@angular/core';
import { Navbar } from '@app/shared/components/navbar/navbar';
import { WhiteSheetBackground } from '@app/shared/components/white-sheet-background/white-sheet-background';
import { DateFilter } from '../trip-history-driver/date-filter/date-filter';
import { AuthService } from '@app/core/services/auth-service';
import { UserEmailInput } from '../block-user/components/user-email-input/user-email-input';

@Component({
  selector: 'app-rides-reports',
  imports: [Navbar, WhiteSheetBackground, DateFilter, UserEmailInput],
  templateUrl: './rides-reports.html',
  styleUrl: './rides-reports.css',
})
export class RidesReports {
  authService = inject(AuthService);
  startDate = signal<string>('');
  endDate = signal<string>('');
  selectedEmail = signal<string>('');
  selectedFilter = signal<'allDrivers' | 'allRegularUsers' | 'oneUser'>('oneUser');

  updateSelectedDate(event: { startDate: string; endDate: string }) {
    this.startDate.set(event.startDate);
    this.endDate.set(event.endDate);
    console.log(`Date range selected: ${event.startDate} to ${event.endDate}`);
  }

  onEmailSelected(event: { email: string }) {
    this.selectedEmail.set(event.email);
    console.log(`Selected email: ${event.email}`);
  }

  onEmailValueChange(email: string) {
    this.selectedEmail.set(email);
  }

  onFilterChange(filter: 'allDrivers' | 'allRegularUsers' | 'oneUser') {
    this.selectedFilter.set(filter);
    console.log(`Selected filter: ${filter}`);
  }

  // Example: Access selected values
  filterReports() {
    const filters = {
      startDate: this.startDate(),
      endDate: this.endDate(),
      email: this.selectedEmail(),
      selectedFilter: this.selectedFilter()
    };
    console.log('Filters:', filters);
    // Use filters to call your report service
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
