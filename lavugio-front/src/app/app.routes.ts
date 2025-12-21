import { Routes } from '@angular/router';
import { ProfileView } from './features/view-profile/profile-view/profile-view';
import { Login } from './features/login/login';
import { ForgotPassword } from './features/forgot-password/forgot-password';
import { ResetPassword } from './features/reset-password/reset-password';
import { Register } from './features/register/register';
import { VerifyEmail } from './features/verify-email/verify-email';
import { VerificationSuccess } from './features/verification-success/verification-success';
import { TripHistoryDriver } from '@app/layout/trip-history-driver/trip-history-driver';
import { BaseInfoPage } from '@app/layout/base-info-page/base-info-page';
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
    path: 'profile', 
    title: "View Profile", 
    component: ProfileView
  },
  {
    path: 'trip-history-driver-detailed', 
    component: TripHistoryDriverDetailed,
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
  }
];