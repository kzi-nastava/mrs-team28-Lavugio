import { Component, inject, signal } from '@angular/core';
import { Navbar } from '@app/shared/components/navbar/navbar';
import { WhiteSheetBackground } from '@app/shared/components/white-sheet-background/white-sheet-background';
import { DateFilter } from '../trip-history-driver/date-filter/date-filter';
import { AuthService } from '@app/core/services/auth-service';
import { UserEmailInput } from '../block-user/components/user-email-input/user-email-input';
import { ChartData, ReportGraph } from './components/report-graph/report-graph';
import {
  DateRangePayload,
  RidesReportsResponse,
  RidesReportsService,
} from '@app/core/services/rides-reports-service';
import { DialogService } from '@app/core/services/dialog-service';

interface ChartConfig {
  title: string;
  xAxisLabel: string;
  yAxisLabel: string;
  chartData: ChartData;
  sum: number;
  average: number;
}

export interface Filters {
  startDate: string;
  endDate: string;
  email: string;
  selectedFilter: 'allDrivers' | 'allRegularUsers' | 'oneUser';
}

@Component({
  selector: 'app-rides-reports',
  imports: [Navbar, WhiteSheetBackground, DateFilter, UserEmailInput, ReportGraph],
  templateUrl: './rides-reports.html',
  styleUrl: './rides-reports.css',
})
export class RidesReports {
  authService = inject(AuthService);
  ridesReportsService = inject(RidesReportsService);
  dialogService = inject(DialogService);

  startDate = signal<string>('');
  endDate = signal<string>('');
  selectedEmail = signal<string>('');
  selectedFilter = signal<'allDrivers' | 'allRegularUsers' | 'oneUser'>('oneUser');

  charts = signal<ChartConfig[]>([
    {
      title: 'Rides Per Day',
      xAxisLabel: 'Date',
      yAxisLabel: 'Rides',
      chartData: { labels: [], data: [] },
      sum: 0,
      average: 0,
    },
    {
      title: 'Mileage Covered Per Day',
      xAxisLabel: 'Date',
      yAxisLabel: 'Mileage (km)',
      chartData: { labels: [], data: [] },
      sum: 0,
      average: 0,
    },
    {
      title: 'Daily Financial Report',
      xAxisLabel: 'Date',
      yAxisLabel: 'Revenue (RSD)',
      chartData: { labels: [], data: [] },
      sum: 0,
      average: 0,
    },
  ]);

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
    const filters: Filters = {
      startDate: this.startDate(),
      endDate: this.endDate(),
      email: this.selectedEmail(),
      selectedFilter: this.selectedFilter(),
    };
    console.log('Filters:', filters);
    this.loadReportsForRole(filters);
  }

  loadReportsForRole(filters: Filters) {
    if (this.authService.isAdmin()) {
      this.loadAdminReports(filters);
      return;
    }

    if (this.authService.isDriver() || this.authService.isRegularUser()) {
      this.loadNonAdminReports(filters);
      return;
    }
  }

  ngOnInit() {
    //this.loadReportsForRole();
  }

  loadAllChartsData() {
    // Simulacija poziva backend-a za sve grafove
    setTimeout(() => {
      this.charts.update((charts) => {
        // Graf 1 - Sales
        charts[0].chartData = {
          labels: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun'],
          data: [1200, 1900, 1500, 2100, 1800, 2400],
        };
        charts[0].sum = charts[0].chartData.data.reduce((a, b) => a + b, 0);
        charts[0].average = charts[0].sum / charts[0].chartData.data.length;

        // Graf 2 - Engagement
        charts[1].chartData = {
          labels: ['Week 1', 'Week 2', 'Week 3', 'Week 4'],
          data: [450, 520, 480, 590],
        };
        charts[1].sum = charts[1].chartData.data.reduce((a, b) => a + b, 0);
        charts[1].average = charts[1].sum / charts[1].chartData.data.length;

        // Graf 3 - Revenue
        charts[2].chartData = {
          labels: ['Product A', 'Product B', 'Product C', 'Product D'],
          data: [3200, 4100, 2800, 3600],
        };
        charts[2].sum = charts[2].chartData.data.reduce((a, b) => a + b, 0);
        charts[2].average = charts[2].sum / charts[2].chartData.data.length;

        return [...charts];
      });
    }, 1000);
  }

  private loadAdminReports(filters: Filters) {
    if (!filters.startDate || !filters.endDate) {
      this.dialogService.open(
        'Error',
        'Please select a valid date range before loading reports.',
        true,
      );
      return;
    }

    if (this.isDateRangeGreaterThanTwoWeeks(filters.startDate, filters.endDate)) {
      this.dialogService.open('Error', 'Please select a date range of 2 weeks or less.', true);
      return;
    }

    if (filters.selectedFilter === 'oneUser' && !filters.email) {
      this.dialogService.open(
        'Error',
        'Please enter a valid email address for the selected user filter.',
        true,
      );
      return;
    }

    if (!this.isValidEmail(filters.email) && filters.selectedFilter === 'oneUser') {
      this.dialogService.open('Error', 'Please enter a valid email address.', true);
      return;
    }

    this.ridesReportsService.getReportsForAdmin(filters).subscribe({
      next: (response) => this.applyChartsResponse(response),
      error: (error) =>
        this.dialogService.open(
          'Error',
          'Failed to load rides reports. Please try again later.',
          true,
        ),
    });
  }

  private loadNonAdminReports(filters: Filters) {
    if (!filters.startDate || !filters.endDate) {
      this.dialogService.open(
        'Error',
        'Please select a valid date range before loading reports.',
        true,
      );
      return;
    }

    if (this.isDateRangeGreaterThanTwoWeeks(filters.startDate, filters.endDate)) {
      this.dialogService.open('Error', 'Please select a date range of 2 weeks or less.', true);
      return;
    }

    const payload: DateRangePayload = { startDate: filters.startDate, endDate: filters.endDate };
    this.ridesReportsService.getReportsForCurrentUser(payload).subscribe({
      next: (response) => this.applyChartsResponse(response),
      error: (error) =>
        this.dialogService.open(
          'Error',
          'Failed to load rides reports. Please try again later.',
          true,
        ),
    });
  }

  private applyChartsResponse(response: RidesReportsResponse) {
    this.charts.set(
      response.charts.map((chart) => ({
        title: chart.title,
        xAxisLabel: chart.xAxisLabel,
        yAxisLabel: chart.yAxisLabel,
        chartData: { labels: chart.labels, data: chart.data },
        sum: chart.sum,
        average: chart.average,
      })),
    );
  }

  isDateRangeGreaterThanTwoWeeks(startDate: string, endDate: string): boolean {
    const parseDate = (dateStr: string): Date => {
      const [day, month, year] = dateStr.split('/').map(Number);
      return new Date(year, month - 1, day);
    };

    const start = parseDate(startDate);
    const end = parseDate(endDate);

    const diffInMs = end.getTime() - start.getTime();

    const diffInDays = diffInMs / (1000 * 60 * 60 * 24);

    return diffInDays > 14;
  }

  isValidEmail(email: string): boolean {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
  }
}
