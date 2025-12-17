import { Routes } from '@angular/router';
import {TripHistoryDriver} from '@app/layout/trip-history-driver/trip-history-driver';
import {BaseInfoPage} from '@app/layout/base-info-page/base-info-page';

export const routes: Routes = [
  {
    path: 'trip-history-driver',
    component: TripHistoryDriver,
  },
  {
    path: '',
    component: BaseInfoPage,
  },
];
