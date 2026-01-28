import { Routes } from '@angular/router';
import { ProfileView } from './features/view-profile/profile-view/profile-view';
import { Login } from './features/login/login';
import { ForgotPassword } from './features/forgot-password/forgot-password';
import { ResetPassword } from './features/reset-password/reset-password';
import { Register } from './features/register/register';
import { VerifyEmail } from './features/verify-email/verify-email';
import { VerificationSuccess } from './features/verification-success/verification-success';
import { RideHistoryDriver as RideHistoryDriver } from '@app/features/trip-history-driver/trip-history-driver';
import { BaseInfoPage } from '@app/features/base-info-page/base-info-page';
import { RideHistoryDriverDetailed as RideHistoryDriverDetailed } from './features/trip-history-driver-detailed/trip-history-driver-detailed';
import {GuestHomePage} from '@app/features/guest-home-page/guest-home-page';
import { FindTrip } from './features/find-trip/find-trip/find-trip';
import { RideOverview } from './features/ride-overview/ride-overview';
import { RegisterDriver } from './features/register-driver/register-driver';
import { CancelRide } from './features/cancel-ride/cancel-ride';
import { DriverActivation } from './features/driver-activation/driver-activation';
import { DriverScheduledRides } from './features/driver-scheduled-rides/driver-scheduled-rides';
import { AdminPanel } from './features/admin-panel/admin-panel';
import { DriverUpdateRequests } from './features/driver-update-requests/driver-update-requests';
import { BlockUser } from './features/block-user/block-user';
import { guestOnlyauthGuardFn } from '@app/core/guards/guestOnlyAuthGuard';
import {AuthGuard} from '@app/core/guards/authGuard';
import {RideOverviewAccessGuard} from '@app/core/guards/rideOverviewAccessGuard'
import { ActiveRides } from './features/active-rides/active-rides';

export const routes: Routes = [
  {
    path: 'ride-history-driver',
    component: RideHistoryDriver,
    canActivate: [AuthGuard],
    data:{role:['DRIVER']}
  },
  {
    path: '',
    component: BaseInfoPage,
    canActivate: [AuthGuard],
    data: {role:[]}
  },
  {
    path: 'profile',
    title: "View Profile",
    component: ProfileView,
    canActivate: [AuthGuard],
    data:{role:['DRIVER', 'REGULAR_USER', 'ADMIN']}
  },
  {
    path: 'ride-history-driver/:rideId',
    component: RideHistoryDriverDetailed,
    canActivate: [AuthGuard],
    data:{role:['DRIVER']}
  },
  {
    path: 'find-trip',
    title: "Find Trip ",
    component: FindTrip,
    canActivate: [AuthGuard],
    data: {role:['REGULAR_USER']}
  },
  {
    path: 'active-rides',
    title: 'My Active Rides',
    component: ActiveRides,
    canActivate: [AuthGuard],
    data: {role: ['REGULAR_USER']}
  },
  {
    path: 'login',
    title: 'Login',
    component: Login,
    canActivate: [guestOnlyauthGuardFn]
  },
  {
    path: 'register',
    title: 'Register',
    component: Register,
    canActivate: [guestOnlyauthGuardFn]
  },
  {
    path: 'register-driver',
    title: 'Register-driver',
    component: RegisterDriver,
    canActivate: [AuthGuard],
    data: {role:['ADMIN']}
  },
  {
    path: 'activate-account',
    component: DriverActivation,
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
    component: ForgotPassword,
    canActivate: [AuthGuard],
    data: {role: ['REGULAR_USER', 'DRIVER', 'ADMIN']}
  },
  {
    path: 'reset-password',
    title: 'Reset Password',
    component: ResetPassword,
    canActivate: [AuthGuard],
    data: {role: ['REGULAR_USER', 'DRIVER', 'ADMIN']}
  },
  {
    path: 'home-page',
    title: 'Home Page',
    component: GuestHomePage,
    canActivate: [guestOnlyauthGuardFn]
  },
  {
    path: ':rideId/ride-overview',
    title: 'Ride Overview',
    component: RideOverview,
    canActivate: [RideOverviewAccessGuard],
    data: {role:['REGULAR_USER']}
  }
  ,
  {
    path: 'cancel-ride/:rideId',
    title: 'Otkazivanje vožnje',
    component: CancelRide,
    canActivate: [AuthGuard],
    data: {role: ['REGULAR_USER']}
  },
  {
    path:'driver-scheduled-rides',
    title: 'Scheduled rides',
    component: DriverScheduledRides,
    canActivate: [AuthGuard],
    data: {role: ['DRIVER']}
  },
  {
    path: 'admin-panel',
    title: 'Admin Panel',
    component: AdminPanel,
    canActivate: [AuthGuard],
    data: {role: ['ADMIN']}
  },
  {
    path: 'driver-update-requests',
    title: 'Driver Update Requests',
    component: DriverUpdateRequests,
    canActivate: [AuthGuard],
    data: {role: ['ADMIN']}
  },
  {
    path: 'block-user',
    title: 'Block User',
    component: BlockUser,
    canActivate: [AuthGuard],
    data: {role: ['ADMIN']}
  }
];
