import { Routes } from '@angular/router';
import { ProfileView } from './features/view-profile/profile-view/profile-view';
import { Login } from './features/login/login';
import { ForgotPassword } from './features/forgot-password/forgot-password';
import { ResetPassword } from './features/reset-password/reset-password';
import { Register } from './features/register/register';
import { VerifyEmail } from './features/verify-email/verify-email';
import { VerificationSuccess } from './features/verification-success/verification-success';
import { TripHistoryDriver } from '@app/features/trip-history-driver/trip-history-driver';
import { BaseInfoPage } from '@app/features/base-info-page/base-info-page';
import { TripHistoryDriverDetailed } from './features/trip-history-driver-detailed/trip-history-driver-detailed';
import {GuestHomePage} from '@app/features/guest-home-page/guest-home-page';
import { FindTrip } from './features/find-trip/find-trip/find-trip';
import { RideOverview } from './features/ride-overview/ride-overview';
import { RegisterDriver } from './features/register-driver/register-driver';
import { CancelRide } from './features/cancel-ride/cancel-ride';
import { DriverActivation } from './features/driver-activation/driver-activation';
import { DriverScheduledRides } from './features/driver-scheduled-rides/driver-scheduled-rides';
import { AdminPanel } from './features/admin-panel/admin-panel';
import { DriverUpdateRequests } from './features/driver-update-requests/driver-update-requests';

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
    path: 'profile',
    title: "View Profile",
    component: ProfileView
  },
  {
    path: 'trip-history-driver-detailed',
    component: TripHistoryDriverDetailed,
  },
  {
        path: 'find-trip', title: "Find Trip ", component: FindTrip
  },
  {
    path: 'login',
    title: 'Login',
    component: Login
  },
  {
    path: 'register',
    title: 'Register',
    component: Register
  },
  {
    path: 'register-driver',
    title: 'Register-driver',
    component: RegisterDriver
  },
  {
    path: 'activate-account',
    component: DriverActivation
  },
  {
    path: 'verify-email',
    title: 'Verify Email',
    component: VerifyEmail
  },
  {
    path: 'verification-success',
    title: 'Verification Success',
    component: VerificationSuccess
  },
  {
    path: 'forgot-password',
    title: 'Forgot Password',
    component: ForgotPassword
  },
  {
    path: 'reset-password',
    title: 'Reset Password',
    component: ResetPassword
  },
  {
    path: 'home-page',
    title: 'Home Page',
    component: GuestHomePage
  },
  {
    path: ':rideId/ride-overview',
    title: 'Ride Overview',
    component: RideOverview
  }
  ,
  {
    path: 'cancel-ride/:rideId',
    title: 'Otkazivanje vožnje',
    component: CancelRide
  },
  {
    path:'driver-scheduled-rides',
    title: 'Scheduled rides',
    component: DriverScheduledRides
  },
  {
    path: 'admin-panel',
    title: 'Admin Panel',
    component: AdminPanel
  },
  {
    path: 'driver-update-requests',
    title: 'Driver Update Requests',
    component: DriverUpdateRequests
  }
];
