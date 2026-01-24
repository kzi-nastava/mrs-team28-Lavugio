import { Component, signal, effect } from '@angular/core';
import { ProfileHeader } from '../components/profile-header/profile-header';
import { ProfileInfoSection } from '../components/profile-info-section/profile-info-section';
import { Navbar } from '@app/shared/components/navbar/navbar';
import { UserService } from '@app/core/services/user/user-service';
import { UserProfile } from '@app/shared/models/user/userProfile';

@Component({
  selector: 'app-profile-view',
  imports: [ProfileHeader, ProfileInfoSection, Navbar],
  templateUrl: './profile-view.html',
  styleUrl: './profile-view.css',
})
export class ProfileView {
  
  userProfile = signal<UserProfile | null>(null);
  isDriver = signal<boolean>(false);

  constructor(private userService: UserService) {}

  ngOnInit() {
    this.userService.getUserProfile().subscribe({
      next: (profile) => {
        console.log("Loaded profile:", profile);
        this.userProfile.set(profile);
        this.isDriver.set(profile.role === "DRIVER");
      },
      error: (err) => {
        console.error("Failed to load profile:", err);
      }
    });
  }
}
