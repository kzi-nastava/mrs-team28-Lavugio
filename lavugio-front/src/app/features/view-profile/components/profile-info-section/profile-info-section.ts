import { Component, computed, inject, Input, signal } from '@angular/core';
import { ProfileInfoRow } from '../profile-info-row/profile-info-row';
import { Button } from './../../../../shared/components/button/button';
import { ProfileEdit } from '../../services/profile-edit';
import { UserProfile } from '@app/shared/models/user/userProfile';
import { UserService } from '@app/core/services/user/user-service';
import { DialogService } from '@app/core/services/dialog-service';
import { ChangePasswordDialog } from '../change-password-dialog/change-password-dialog';
import { DriverService } from '@app/core/services/user/driver-service';
import { EditProfileDTO, MapProfileToEditDriverProfileRequestDTO } from '@app/shared/models/user/editProfileDTO';
import { EditDriverProfileRequestDTO } from '@app/shared/models/user/editProfileDTO';

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
  vehicleTypeOptions = ['Standard', 'Luxury', 'Combi'];

  private userService = inject(UserService);
  private dialogService = inject(DialogService);
  private driverService = inject(DriverService);

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
    if (field === 'vehicleSeats') {
      this.updatedProfile[field] = Number(value);
    } else {
      (this.updatedProfile[field] as string) = value;
    }
  }

  onEditClick() {
    console.log("aaa");
    if (this.editService.isEditMode()) {
      console.log('Validating and saving profile:', this.updatedProfile);
      var validateProfileUpdate = this.validateProfileUpdate();
      if (validateProfileUpdate !== true) {
        this.dialogService.open('Validation Error', validateProfileUpdate as string, true);
        return;
      }
      if (this.profile.role === 'DRIVER') {
        this.sendEditRequest();
      } else {
        this.updateProfile();
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

  private updateProfile() {
    console.log('Start profile:', this.profile);
    console.log('Updated profile:', this.updatedProfile);
    const edit: EditProfileDTO = {
      name: this.updatedProfile.name,
      surname: this.updatedProfile.surname,
      phoneNumber: this.updatedProfile.phoneNumber,
      address: this.updatedProfile.address,
    };
    this.userService.updateProfile(this.updatedProfile).subscribe({
      next: () => {
        const title: string = "Update Successful!";
        const message: string = "Profile updated successfully!";
        const dialogRef = this.dialogService.open(title, message, false);
        dialogRef.afterClosed().subscribe(() => {
          this.editService.disableEditMode();
          window.location.reload();
        });
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
    const editRequest: EditDriverProfileRequestDTO = MapProfileToEditDriverProfileRequestDTO(this.updatedProfile);
    this.driverService.sendEditRequest(editRequest).subscribe({
      next: () => {
        console.log('Edit request sent successfully');
        this.dialogService.open('Request Sent', 'Your edit request has been sent to the administrator for approval.', false);
        this.editService.disableEditMode();
      },
      error: (error) => {
        console.error('Error sending edit request:', error);
        this.dialogService.open('Error', 'There was an error sending your edit request. Please try again later.', true);
        this.editService.disableEditMode();
      }
    });
  }

  private validateProfileUpdate(): string | boolean {
    
    const phoneFormat1Regex = /^06\d{5,9}$/;
    const phoneFormat2Regex = /^\+381\d{6,10}$/
    if (!phoneFormat1Regex.test(this.updatedProfile.phoneNumber) && !phoneFormat2Regex.test(this.updatedProfile.phoneNumber)) {
      return "Phone number format is invalid.";
    }
    
    if (this.updatedProfile.role === 'DRIVER') {
      const seatsNum = Number(this.updatedProfile.vehicleSeats);
      if (!Number.isInteger(seatsNum) || seatsNum < 1) {
        return "Vehicle seats number must be a positive integer.";
      }
      const licensePlateRegex = /^[A-Z]{2}-\d{3,5}-[A-Z]{2}$/;
      if (!licensePlateRegex.test(this.updatedProfile.vehicleLicensePlate || '')) {
        return "License plate format is invalid.";
      }
    }

    return true;
  }

  getSeatsString(seats?: number): string {
    if (seats === undefined || seats === null) {
      return '';
    }
    return seats.toString();
  }

  getVehicleTypeString(type?: string): string {
    if (!type) return '';
    switch (type) {
      case 'STANDARD':
        return 'Standard';
      case 'LUXURY':
        return 'Luxury';
      case 'COMBI':
        return 'Combi';
      default:
        return '';
    }
  }

}
