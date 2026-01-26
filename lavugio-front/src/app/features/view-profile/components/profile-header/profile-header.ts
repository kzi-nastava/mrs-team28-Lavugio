import { Component, Input, inject, OnInit, OnDestroy, ChangeDetectorRef } from '@angular/core';
import { ProfileEdit } from '../../services/profile-edit';
import { UserProfile } from '@app/shared/models/user/userProfile';
import { UserService } from '@app/core/services/user/user-service';
import { environment } from 'environments/environment';
import { HttpClient } from '@angular/common/http';
import { DomSanitizer, SafeUrl } from '@angular/platform-browser';

@Component({
  selector: 'app-profile-header',
  imports: [],
  templateUrl: './profile-header.html',
  styleUrl: './profile-header.css',
})
export class ProfileHeader implements OnInit, OnDestroy {
  @Input() profile!: UserProfile;

  editService = inject(ProfileEdit);
  userService = inject(UserService);
  http = inject(HttpClient);
  sanitizer = inject(DomSanitizer);
  cdr = inject(ChangeDetectorRef);
  isUploading = false;
  previewUrl: string | null = null;
  profileImageUrl: SafeUrl | null = null;
  private imageBlobUrl: string | null = null;

  ngOnInit() {
    this.loadProfileImage();
  }

  ngOnDestroy() {
    if (this.imageBlobUrl) {
      URL.revokeObjectURL(this.imageBlobUrl);
    }
  }

  loadProfileImage() {
    console.log('Loading profile image from:', `${environment.BACKEND_URL}/api/users/profile-photo`);
    this.http.get(`${environment.BACKEND_URL}/api/users/profile-photo`, { 
      responseType: 'blob' 
    }).subscribe({
      next: (blob) => {
        console.log('Received blob:', blob, 'Size:', blob.size, 'Type:', blob.type);

        if (this.imageBlobUrl) {
          URL.revokeObjectURL(this.imageBlobUrl);
        }

        this.imageBlobUrl = URL.createObjectURL(blob);
        console.log('Created blob URL:', this.imageBlobUrl);
        this.profileImageUrl = this.sanitizer.bypassSecurityTrustUrl(this.imageBlobUrl);
        console.log('Profile image URL set:', this.profileImageUrl);

        this.cdr.detectChanges();
      },
      error: (error) => {
        console.error('Failed to load profile image:', error);
        this.profileImageUrl = null;
        this.cdr.detectChanges();
      }
    });
  }

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
        this.previewUrl = null;
        this.loadProfileImage();
        this.isUploading = false;
      },
      error: (error) => {
        console.error('Upload failed:', error);
        this.isUploading = false;
      },
    });
  }

  getProfileImageUrl(): SafeUrl | string {
    if (this.previewUrl) {
      console.log('Returning preview URL');
      return this.sanitizer.bypassSecurityTrustUrl(this.previewUrl);
    }
    if (this.profileImageUrl) {
      console.log('Returning profile image URL:', this.profileImageUrl);
      return this.profileImageUrl;
    }
    const name = this.profile ? `${this.profile.name}` : 'User';
    return `https://ui-avatars.com/api/?name=${name}&background=606C38&color=fff&size=200`;
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
