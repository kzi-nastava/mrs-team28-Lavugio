import { Component } from '@angular/core';
import { ProfileHeader } from '../components/profile-header/profile-header';
import { ProfileInfoSection } from '../components/profile-info-section/profile-info-section';

@Component({
  selector: 'app-profile-view',
  imports: [ProfileHeader, ProfileInfoSection],
  templateUrl: './profile-view.html',
  styleUrl: './profile-view.css',
})
export class ProfileView {

}
