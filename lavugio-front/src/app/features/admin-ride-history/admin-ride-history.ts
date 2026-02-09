import { Component, inject, OnInit, signal } from '@angular/core';
import { BaseInfoPage } from '../base-info-page/base-info-page';
import { DateRangePicker } from '@app/shared/components/date-range-picker/date-range-picker';
import { AdminService } from '@app/core/services/admin-service';
import { RideHistoryAdminModel, RideHistoryAdminPagingModel } from '@app/shared/models/ride/rideHistoryAdmin';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-admin-ride-history',
  imports: [BaseInfoPage, DateRangePicker, CommonModule, FormsModule],
  templateUrl: './admin-ride-history.html',
  styleUrl: './admin-ride-history.css',
})
export class AdminRideHistory implements OnInit {
  adminService = inject(AdminService);
  router = inject(Router);
  activatedRoute = inject(ActivatedRoute);

  email = signal('');
  rides = signal<RideHistoryAdminModel[]>([]);
  loading = signal(false);
  hasMoreOlder = signal(true);
  currentPage = signal(0);

  pageSize = 10;
  sorting: 'ASC' | 'DESC' = 'DESC';
  sortBy: 'START' | 'DEPARTURE' | 'DESTINATION' | 'PRICE' | 'CANCELLED' | 'PANIC' = 'START';

  startDate = '01/01/2000';
  endDate = '31/12/2100';

  ngOnInit() {
    const emailParam = this.activatedRoute.snapshot.queryParamMap.get('email');
    if (emailParam) {
      this.email.set(emailParam);
      this.loadRides();
    }
  }

  searchByEmail() {
    if (!this.email().trim()) return;
    this.currentPage.set(0);
    this.rides.set([]);
    this.loadRides();
  }

  loadRides() {
    if (this.loading() || !this.email().trim()) return;

    this.loading.set(true);

    this.adminService
      .getUserRideHistory(
        this.email(),
        this.currentPage(),
        this.pageSize,
        this.sorting,
        this.sortBy,
        this.startDate,
        this.endDate
      )
      .subscribe({
        next: (response: RideHistoryAdminPagingModel) => {
          if (this.currentPage() === 0) {
            this.rides.set(response.adminHistory);
          } else {
            this.rides.update((current) => [...current, ...response.adminHistory]);
          }
          this.hasMoreOlder.set(!response.reachedEnd);
          this.loading.set(false);
        },
        error: (err) => {
          console.error('Error while loading', err);
          this.loading.set(false);
        },
      });
  }

  loadMore() {
    if (!this.hasMoreOlder() || this.loading()) return;
    this.currentPage.update(p => p + 1);
    this.loadRides();
  }

  updateSortBy(field: 'START' | 'DEPARTURE' | 'DESTINATION' | 'PRICE' | 'CANCELLED' | 'PANIC') {
    if (this.sortBy === field) {
      this.sorting = this.sorting === 'ASC' ? 'DESC' : 'ASC';
    } else {
      this.sortBy = field;
      this.sorting = 'DESC';
    }
    this.sortRides();
  }

  sortRides() {
    const sorted = [...this.rides()].sort((a, b) => {
      let comparison = 0;
      
      switch (this.sortBy) {
        case 'START':
          comparison = this.compareDates(a.startDate, b.startDate);
          break;
        case 'DEPARTURE':
          comparison = a.startAddress.localeCompare(b.startAddress);
          break;
        case 'DESTINATION':
          comparison = a.endAddress.localeCompare(b.endAddress);
          break;
        case 'PRICE':
          comparison = a.price - b.price;
          break;
        case 'CANCELLED':
          comparison = (a.cancelled ? 1 : 0) - (b.cancelled ? 1 : 0);
          break;
        case 'PANIC':
          comparison = (a.panic ? 1 : 0) - (b.panic ? 1 : 0);
          break;
      }
      
      return this.sorting === 'ASC' ? comparison : -comparison;
    });
    
    this.rides.set(sorted);
  }

  compareDates(dateA: string, dateB: string): number {
    // Format is "HH:mm dd.MM.yyyy"
    const parseDate = (str: string): Date => {
      if (!str) return new Date(0);
      const parts = str.trim().split(' ');
      if (parts.length !== 2) return new Date(0);
      const [time, date] = parts;
      const [hours, minutes] = time.split(':').map(Number);
      const [day, month, year] = date.split('.').map(Number);
      return new Date(year, month - 1, day, hours, minutes);
    };
    return parseDate(dateA).getTime() - parseDate(dateB).getTime();
  }

  updateDateRange(event: { startDate: string; endDate: string }) {
    this.startDate = event.startDate;
    this.endDate = event.endDate;
    this.currentPage.set(0);
    this.rides.set([]);
    this.loadRides();
  }

  parseDateTime(dateTimeStr: string): { date: string; time: string } {
    if (!dateTimeStr) return { date: '', time: '' };
    const parts = dateTimeStr.trim().split(' ');
    if (parts.length === 2) {
      return { time: parts[0], date: parts[1] };
    }
    return { date: '', time: '' };
  }

  openDetails(rideId: number) {
    this.router.navigate([`/admin-ride-history/${rideId}`], {
      queryParams: { email: this.email() }
    });
  }

  getSortIcon(field: string): string {
    if (this.sortBy !== field) return '';
    return this.sorting === 'ASC' ? '↑' : '↓';
  }
}
