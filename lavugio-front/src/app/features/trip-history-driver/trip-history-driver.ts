import { Component, inject, signal } from '@angular/core';
import { BaseInfoPage } from '../base-info-page/base-info-page';
import { DateFilter } from './date-filter/date-filter';
import { Table } from './table/table';
import { RideService } from '@app/core/services/ride-service';
import { RideHistoryDriverModel } from '@app/shared/models/ride/rideHistoryDriver';
import { DriverService } from '@app/core/services/driver-service';
import { RideHistoryDriverPagingModel } from '@app/shared/models/ride/rideHistoryDriverPagingModel';

@Component({
  selector: 'app-trip-history-driver',
  imports: [BaseInfoPage, DateFilter, Table],
  templateUrl: './trip-history-driver.html',
  styleUrl: './trip-history-driver.css',
})
export class RideHistoryDriver {
  driverService = inject(DriverService);
  
  oldestLoadedPage = signal(0);
  newestLoadedPage = signal(0);
  
  hasMoreNewer = signal(false);
  hasMoreOlder = signal(true);
  
  pageSize = 10;
  maxLoadedPages = 5; 
  sorting: 'ASC' | 'DESC' = 'DESC';
  sortBy: 'START' | 'DEPARTURE' | 'DESTINATION' = 'START';
  
  rides = signal<RideHistoryDriverModel[]>([]);
  loading = signal(false);
  loadingDirection = signal<'none' | 'older' | 'newer'>('none');
  
  driverId = 1;
  startDate = '2024-01-01';
  endDate = '2025-12-31';

  constructor() {
    this.loadInitialRides();
  }

  loadInitialRides() {
    this.loading.set(true);
    this.loadingDirection.set('none');

    this.driverService
      .getDriverRideHistory(
        this.driverId,
        0,
        this.pageSize,
        this.sorting,
        this.sortBy,
        this.startDate,
        this.endDate
      )
      .subscribe({
        next: (response: RideHistoryDriverPagingModel) => {
          this.rides.set(response.driverHistory);
          this.oldestLoadedPage.set(0);
          this.newestLoadedPage.set(0);
          this.hasMoreOlder.set(!response.reachedEnd);
          this.hasMoreNewer.set(false);
          this.loading.set(false);
          this.loadingDirection.set('none');
        },
        error: (err) => {
          console.error('Error while loading', err);
          this.loading.set(false);
          this.loadingDirection.set('none');
        }
      });
  }

  loadOlder() {
    if (this.loading() || !this.hasMoreOlder()) return;

    const nextPage = this.newestLoadedPage() + 1;
    this.loading.set(true);
    this.loadingDirection.set('older');

    this.driverService
      .getDriverRideHistory(
        this.driverId,
        nextPage,
        this.pageSize,
        this.sorting,
        this.sortBy,
        this.startDate,
        this.endDate
      )
      .subscribe({
        next: (response: RideHistoryDriverPagingModel) => {
          this.rides.update(current => [...current, ...response.driverHistory]);
          this.newestLoadedPage.set(nextPage);
          this.hasMoreOlder.set(!response.reachedEnd);
          
          this.trimOldPages();
          
          this.loading.set(false);
          this.loadingDirection.set('none');
        },
        error: (err) => {
          console.error('Error while loading older rides', err);
          this.loading.set(false);
          this.loadingDirection.set('none');
        }
      });
  }

  loadNewer() {
    if (this.loading() || !this.hasMoreNewer()) return;

    const nextPage = this.oldestLoadedPage() - 1;
    if (nextPage < 0) return;

    this.loading.set(true);
    this.loadingDirection.set('newer');

    this.driverService
      .getDriverRideHistory(
        this.driverId,
        nextPage,
        this.pageSize,
        this.sorting,
        this.sortBy,
        this.startDate,
        this.endDate
      )
      .subscribe({
        next: (response: RideHistoryDriverPagingModel) => {
          this.rides.update(current => [...response.driverHistory, ...current]);
          this.oldestLoadedPage.set(nextPage);
          this.hasMoreNewer.set(nextPage > 0);
          this.trimNewPages();
          
          this.loading.set(false);
          this.loadingDirection.set('none');
        },
        error: (err) => {
          console.error('Error while loading newer rides', err);
          this.loading.set(false);
          this.loadingDirection.set('none');
        }
      });
  }

  private trimOldPages() {
    const totalLoadedPages = this.newestLoadedPage() - this.oldestLoadedPage() + 1;
    
    if (totalLoadedPages > this.maxLoadedPages) {
      const pagesToRemove = totalLoadedPages - this.maxLoadedPages;
      const itemsToRemove = pagesToRemove * this.pageSize;
      
      this.rides.update(current => current.slice(itemsToRemove));
      this.oldestLoadedPage.update(p => p + pagesToRemove);
      this.hasMoreNewer.set(true);
    }
  }

  private trimNewPages() {
    const totalLoadedPages = this.newestLoadedPage() - this.oldestLoadedPage() + 1;
    
    if (totalLoadedPages > this.maxLoadedPages) {
      const pagesToRemove = totalLoadedPages - this.maxLoadedPages;
      const itemsToRemove = pagesToRemove * this.pageSize;
      
      this.rides.update(current => current.slice(0, -itemsToRemove));
      this.newestLoadedPage.update(p => p - pagesToRemove);
      this.hasMoreOlder.set(true);
    }
  }
}