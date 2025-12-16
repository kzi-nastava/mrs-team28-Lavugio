import { Routes } from '@angular/router';
import { ProfileView } from './features/view-profile/profile-view/profile-view';

export const routes: Routes = [
    {
        path: 'profile', title: "View Profile", component: ProfileView
    }
];
