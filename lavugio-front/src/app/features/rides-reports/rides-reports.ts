import { Component, inject, signal } from '@angular/core';
import { Navbar } from '@app/shared/components/navbar/navbar';
import { WhiteSheetBackground } from '@app/shared/components/white-sheet-background/white-sheet-background';
import { DateFilter } from '../trip-history-driver/date-filter/date-filter';
import { AuthService } from '@app/core/services/auth-service';
import { UserEmailInput } from '../block-user/components/user-email-input/user-email-input';
import { ChartData, ReportGraph } from './components/report-graph/report-graph';

interface ChartConfig {
  title: string;
  xAxisLabel: string;
  yAxisLabel: string;
  chartData: ChartData;
  sum: number;
  average: number;
}

@Component({
  selector: 'app-rides-reports',
  imports: [Navbar, WhiteSheetBackground, DateFilter, UserEmailInput, ReportGraph],
  templateUrl: './rides-reports.html',
  styleUrl: './rides-reports.css',
})
export class RidesReports {
  authService = inject(AuthService);
  startDate = signal<string>('');
  endDate = signal<string>('');
  selectedEmail = signal<string>('');
  selectedFilter = signal<'allDrivers' | 'allRegularUsers' | 'oneUser'>('oneUser');

  charts = signal<ChartConfig[]>([
    {
      title: 'Monthly Sales Report',
      xAxisLabel: 'Months',
      yAxisLabel: 'Sales ($)',
      chartData: { labels: [], data: [] },
      sum: 0,
      average: 0
    },
    {
      title: 'Customer Engagement',
      xAxisLabel: 'Weeks',
      yAxisLabel: 'Visits',
      chartData: { labels: [], data: [] },
      sum: 0,
      average: 0
    },
    {
      title: 'Revenue by Product',
      xAxisLabel: 'Products',
      yAxisLabel: 'Revenue ($)',
      chartData: { labels: [], data: [] },
      sum: 0,
      average: 0
    }
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

  ngOnInit() {
    // Initially empty chart, load data from backend
    this.loadAllChartsData();
  }

  loadAllChartsData() {
    // Simulacija poziva backend-a za sve grafove
    setTimeout(() => {
      this.charts.update(charts => {
        // Graf 1 - Sales
        charts[0].chartData = {
          labels: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun'],
          data: [1200, 1900, 1500, 2100, 1800, 2400]
        };
        charts[0].sum = charts[0].chartData.data.reduce((a, b) => a + b, 0);
        charts[0].average = charts[0].sum / charts[0].chartData.data.length;

        // Graf 2 - Engagement
        charts[1].chartData = {
          labels: ['Week 1', 'Week 2', 'Week 3', 'Week 4'],
          data: [450, 520, 480, 590]
        };
        charts[1].sum = charts[1].chartData.data.reduce((a, b) => a + b, 0);
        charts[1].average = charts[1].sum / charts[1].chartData.data.length;

        // Graf 3 - Revenue
        charts[2].chartData = {
          labels: ['Product A', 'Product B', 'Product C', 'Product D'],
          data: [3200, 4100, 2800, 3600]
        };
        charts[2].sum = charts[2].chartData.data.reduce((a, b) => a + b, 0);
        charts[2].average = charts[2].sum / charts[2].chartData.data.length;

        return [...charts];
      });
    }, 1000);
  }

  // Opciono: Ažuriraj specifičan graf
  updateChart(index: number, newData: ChartData) {
    this.charts.update(charts => {
      charts[index].chartData = newData;
      charts[index].sum = newData.data.reduce((a, b) => a + b, 0);
      charts[index].average = charts[index].sum / newData.data.length;
      return [...charts];
    });
  }

  private loadAdminReports() { /* ... */ }
  private loadDriverReports() { /* ... */ }
  private loadUserReports() { /* ... */ }
}
