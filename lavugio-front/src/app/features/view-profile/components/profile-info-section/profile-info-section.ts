import { Component, computed, inject, Input, signal } from '@angular/core';
import { ProfileInfoRow } from '../profile-info-row/profile-info-row';
import { Button } from './../../../../shared/components/button/button';
import { ProfileEdit } from '../../services/profile-edit';
import { UserProfile } from '@app/shared/models/user/userProfile';
import { UserService } from '@app/core/services/user/user-service';
import { DialogService } from '@app/core/services/dialog-service';
import { getRoleString } from '@app/shared/models/user/userProfile';
import { ChangePasswordDialog } from '../change-password-dialog/change-password-dialog';


@Component({
  selector: 'app-profile-info-section',
  standalone: true,
  imports: [ProfileInfoRow, Button, ChangePasswordDialog],
  templateUrl: './profile-info-section.html',
  styleUrl: './profile-info-section.css',
})

export class ProfileInfoSection {
  editService = inject(ProfileEdit);
  
  @Input() profile!: UserProfile;
  updatedProfile!: UserProfile;
  showPasswordDialog = false;

  constructor(private userService: UserService,
              private dialogService: DialogService
  ) {}

  ngOnInit() {
    this.updatedProfile = structuredClone(this.profile);

  }

  editButtonText = computed(() => {
    if (!this.editService.isEditMode()) {
      return 'Edit';
    }
    if (this.profile.role === 'DRIVER') {
      return 'Send Request';
    }
    return 'Save';
  });

  onFieldChanged(field: keyof UserProfile, value: string) {
    (this.updatedProfile[field] as string) = value;
  }

  onEditClick() {
    if (this.editService.isEditMode()) {
      if (this.profile.role === 'DRIVER') {
        this.sendEditRequest();
      } else {
        this.saveProfile();
      }
    } else {
      this.editService.enableEditMode();
    }
  }

  onActivateClick() {
    console.log('Activate clicked');
    // Implementiraj aktivaciju
  }

  onChangePasswordClick() {
    this.showPasswordDialog = true;
  }

  onPasswordDialogClosed() {
    this.showPasswordDialog = false;
  }

  onPasswordChanged(data: { oldPassword: string; newPassword: string }) {
    console.log('Password change requested:', data);
    
    this.userService.changePassword(data.oldPassword, data.newPassword).subscribe({
      next: () => {
        this.dialogService.open('Success!', 'Password changed successfully!', false);
        this.showPasswordDialog = false;
      },
      error: (error) => {
        console.error('Password change error:', error);
        const errorMessage = error.error?.message || 'Failed to change password!';
        this.dialogService.open('Error!', errorMessage, true);
      }
    });
  }

  private saveProfile() {
    console.log('Start profile:', this.profile);
    console.log('Updated profile:', this.updatedProfile);
    this.userService.updateProfile(this.updatedProfile).subscribe({
      next: () => {
        const title: string = "Update Successful!";
        var message: string = "";
        if (getRoleString(this.profile.role) === "Driver") {
          message = "Update request sent successfully!";  
        } else {
          message = "Profile updated successfully!";  
        }
        this.dialogService.open(title, message, false);
        this.editService.disableEditMode();
      },
      error: (error) => {
        console.log("Error during profile update:", error);
        const title: string = "Update Failed!";
        const message: string = "There was an error with profile update!";
        this.dialogService.open(title, message, true);
      },
      complete: () => {

      }
    });
  }

  private sendEditRequest() {
    console.log('Sending edit request:', this.profile);
    // API call za slanje requesta adminu
    // this.profileService.sendEditRequest(this.profile).subscribe(...)

    // Nakon uspešnog slanja, isključi edit mode
    this.editService.disableEditMode();
  }
}
