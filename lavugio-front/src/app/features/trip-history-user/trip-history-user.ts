import { Component, inject, signal, ViewChild } from '@angular/core';
import { BaseInfoPage } from '../base-info-page/base-info-page';
import { DateFilter } from './date-filter/date-filter';
import { Table } from './table/table';
import { UserService } from '@app/core/services/user/user-service';
import { RideHistoryUserModel } from '@app/shared/models/ride/rideHistoryUser';
import { RideHistoryUserPagingModel } from '@app/shared/models/ride/rideHistoryUserPagingModel';

@Component({
  selector: 'app-trip-history-user',
  imports: [BaseInfoPage, DateFilter, Table],
  templateUrl: './trip-history-user.html',
  styleUrl: './trip-history-user.css',
})
export class TripHistoryUser {
  @ViewChild('table') table!: Table;
  userService = inject(UserService);

  oldestLoadedPage = signal(0);
  newestLoadedPage = signal(0);

  hasMoreNewer = signal(false);
  hasMoreOlder = signal(true);

  pageSize = 10;
  maxLoadedPages = 3;
  sorting: 'ASC' | 'DESC' = 'DESC';
  sortBy: 'START' | 'DEPARTURE' | 'DESTINATION' = 'START';

  rides = signal<RideHistoryUserModel[]>([]);
  loading = signal(false);
  loadingDirection = signal<'none' | 'older' | 'newer'>('none');

  startDate = '01/01/2000';
  endDate = '31/12/2100';

  constructor() {
    this.loadInitialRides();
  }

  loadInitialRides() {
    this.loading.set(true);
    this.loadingDirection.set('none');

    this.userService
      .getUserRideHistory(
        0,
        this.pageSize,
        this.sorting,
        this.sortBy,
        this.startDate,
        this.endDate
      )
      .subscribe({
        next: (response: RideHistoryUserPagingModel) => {
          this.rides.set(response.userHistory);
          this.oldestLoadedPage.set(0);
          this.newestLoadedPage.set(0);
          this.hasMoreOlder.set(!response.reachedEnd);
          this.hasMoreNewer.set(false);
          this.loading.set(false);
          this.loadingDirection.set('none');
          console.log(response);
        },
        error: (err) => {
          console.error('Error while loading', err);
          this.loading.set(false);
          this.loadingDirection.set('none');
        },
      });
  }

  loadOlder() {
    if (this.loading() || !this.hasMoreOlder()) return;

    const nextPage = this.newestLoadedPage() + 1;
    this.loading.set(true);
    this.loadingDirection.set('older');

    this.userService
      .getUserRideHistory(
        nextPage,
        this.pageSize,
        this.sorting,
        this.sortBy,
        this.startDate,
        this.endDate
      )
      .subscribe({
        next: (response: RideHistoryUserPagingModel) => {
          this.rides.update((current) => [...current, ...response.userHistory]);
          this.newestLoadedPage.set(nextPage);
          this.hasMoreOlder.set(!response.reachedEnd);

          this.trimOldPages();

          this.loading.set(false);
          this.loadingDirection.set('none');
          console.log(response);
        },
        error: (err) => {
          console.error('Error while loading older rides', err);
          this.loading.set(false);
          this.loadingDirection.set('none');
        },
      });
  }

  loadNewer() {
    if (this.loading() || !this.hasMoreNewer()) return;

    const nextPage = this.oldestLoadedPage() - 1;
    if (nextPage < 0) return;

    this.loading.set(true);
    this.loadingDirection.set('newer');

    this.userService
      .getUserRideHistory(
        nextPage,
        this.pageSize,
        this.sorting,
        this.sortBy,
        this.startDate,
        this.endDate
      )
      .subscribe({
        next: (response: RideHistoryUserPagingModel) => {
          this.rides.update((current) => [...response.userHistory, ...current]);
          this.oldestLoadedPage.set(nextPage);
          this.hasMoreNewer.set(nextPage > 0);
          this.trimNewPages();

          this.loading.set(false);
          this.loadingDirection.set('none');
          console.log(response);
        },
        error: (err) => {
          console.error('Error while loading newer rides', err);
          this.loading.set(false);
          this.loadingDirection.set('none');
        },
      });
  }

  private trimOldPages() {
    const totalLoadedPages = this.newestLoadedPage() - this.oldestLoadedPage() + 1;

    if (totalLoadedPages > this.maxLoadedPages) {
      const pagesToRemove = totalLoadedPages - this.maxLoadedPages;
      const itemsToRemove = pagesToRemove * this.pageSize;

      this.rides.update((current) => current.slice(itemsToRemove));
      this.oldestLoadedPage.update((p) => p + pagesToRemove);
      this.hasMoreNewer.set(true);
    }
  }

  private trimNewPages() {
    const totalLoadedPages = this.newestLoadedPage() - this.oldestLoadedPage() + 1;

    if (totalLoadedPages > this.maxLoadedPages) {
      const pagesToRemove = totalLoadedPages - this.maxLoadedPages;
      const itemsToRemove = pagesToRemove * this.pageSize;

      this.rides.update((current) => current.slice(0, -itemsToRemove));
      this.newestLoadedPage.update((p) => p - pagesToRemove);
      this.hasMoreOlder.set(true);
    }
  }

  updateSortBy(output: 'START' | 'DEPARTURE' | 'DESTINATION') {
    console.log('filters applied');
    if (this.sortBy == output) {
      if (this.sorting == 'ASC') {
        this.sorting = 'DESC';
      } else {
        this.sorting = 'ASC';
      }
    } else {
      this.sortBy = output;
      this.sorting = 'DESC';
    }
    this.scrollToTop();
  }

  updateSelectedDate(date: { startDate: string; endDate: string }) {
    this.startDate = date.startDate;
    this.endDate = date.endDate;
    this.scrollToTop();
  }

  scrollToTop() {
    this.table.scrollContainer()!.nativeElement.scrollTop = 0;
    this.loadInitialRides();
  }
}
