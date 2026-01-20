import { Component, Input, inject } from '@angular/core';
import { ProfileEdit } from '../../services/profile-edit';
import { UserProfile } from '@app/shared/models/user/userProfile';
import { UserService } from '@app/core/services/user/user-service';
import { environment } from 'environments/environment';

@Component({
  selector: 'app-profile-header',
  imports: [],
  templateUrl: './profile-header.html',
  styleUrl: './profile-header.css',
})
export class ProfileHeader {
  @Input() profile!: UserProfile;

  editService = inject(ProfileEdit);
  userService = inject(UserService);
  isUploading = false;
  previewUrl: string | null = null;

  onAvatarClick(fileInput: HTMLInputElement) {
    if (this.editService.isEditMode()) {
      fileInput.click();
    }
  }

  onFileSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    const file = input.files && input.files[0];
    if (!file) return;
    if (!file.type.startsWith('image/')) return;

    const reader = new FileReader();
    reader.onload = () => {
      this.previewUrl = reader.result as string;
    };
    reader.readAsDataURL(file);

    this.isUploading = true;

    this.userService.uploadProfilePicture(file).subscribe({
      next: (response) => {
        console.log('Upload successful:', response);
        this.profile.profilePhotoPath = response;
        this.isUploading = false;
      },
      error: (error) => {
        console.error('Upload failed:', error);
        this.isUploading = false;
      },
    });
  }

  getProfileImageUrl(): string {
    return `${environment.BACKEND_URL}/users/profile-photo`;
  }

  getRoleString(role: string): string {
    switch (role) {
      case 'DRIVER':
        return 'Driver';
      case 'REGULAR_USER':
        return 'Regular User';
      case 'ADMINISTRATOR':
        return 'Administrator';
      default:
        return 'Unknown';
    }
  }
}
