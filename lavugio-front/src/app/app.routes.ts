import { Routes } from '@angular/router';
//import {TripHistoryDriver} from '@app/layout/trip-history-driver/trip-history-driver';
//import {BaseInfoPage} from '@app/layout/base-info-page/base-info-page';
import { ProfileView } from './features/view-profile/profile-view/profile-view';
import {TripHistoryDriver} from '@app/layout/trip-history-driver/trip-history-driver';
import {BaseInfoPage} from '@app/layout/base-info-page/base-info-page';
import { TripHistoryDriverDetailed } from './layout/trip-history-driver-detailed/trip-history-driver-detailed';

export const routes: Routes = [
  {
    path: 'trip-history-driver',
    component: TripHistoryDriver,
  },
  {
    path: '',
    component: BaseInfoPage,
  },
  {
        path: 'profile', title: "View Profile", component: ProfileView
  },
  {
    path:'trip-history-driver-detailed', component: TripHistoryDriverDetailed,
  }
];
