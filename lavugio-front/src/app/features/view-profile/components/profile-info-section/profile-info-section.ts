import { Component, computed, inject, Input, signal, OnDestroy } from '@angular/core';
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
import { Coordinates } from '@app/shared/models/coordinates';
import { DriverStatusService } from '@app/core/services/driver-status.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-profile-info-section',
  standalone: true,
  imports: [ProfileInfoRow, Button, ChangePasswordDialog],
  templateUrl: './profile-info-section.html',
  styleUrl: './profile-info-section.css',
})

export class ProfileInfoSection implements OnDestroy {
  editService = inject(ProfileEdit);
  
  @Input() profile!: UserProfile;
  updatedProfile!: UserProfile;
  showPasswordDialog = false;
  vehicleTypeOptions = ['Standard', 'Luxury', 'Combi'];
  timeActive = signal<string>('');
  isDriverActive = signal<boolean>(false);

  private userService = inject(UserService);
  private dialogService = inject(DialogService);
  private driverService = inject(DriverService);
  private driverStatusService = inject(DriverStatusService);
  private driverStatusSubscription: Subscription | null = null;

  ngOnInit() {
    this.updatedProfile = structuredClone(this.profile);
    if (this.updatedProfile.vehiclePetFriendly === undefined) {
      this.updatedProfile.vehiclePetFriendly = false;
    }
    if (this.updatedProfile.vehicleBabyFriendly === undefined) {
      this.updatedProfile.vehicleBabyFriendly = false;
    }
    console.log('Initialized profile editing with profile:', this.profile);
    console.log('Updated profile with booleans:', this.updatedProfile);
    
    // Set initial active status if driver
    if (this.profile.role === 'DRIVER') {
      this.isDriverActive.set(this.profile.isActive || false);
      this.fetchActiveTime();
      
      // Subscribe to driver status changes from navbar
      this.driverStatusSubscription = this.driverStatusService.driverStatus$.subscribe(
        status => {
          if (status !== null) {
            this.isDriverActive.set(status);
          }
        }
      );
    }
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
    console.log(`Field changed: ${field} = ${value}`);
    if (field === 'vehicleSeats') {
      this.updatedProfile[field] = Number(value);
    } else if (field === 'vehiclePetFriendly' || field === 'vehicleBabyFriendly') {
      const boolValue = value === 'true';
      (this.updatedProfile[field] as boolean) = boolValue;
      console.log(`Set ${field} to ${boolValue}`);
    } else {
      (this.updatedProfile[field] as string) = value;
    }
    console.log('Updated profile after change:', this.updatedProfile);
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
      this.driverService.activateDriver().subscribe({
        next: () => {
          this.dialogService.open('Success', 'Driver activated successfully!', false);
          this.isDriverActive.set(true);
          this.profile.isActive = true;
          setTimeout(() => window.location.reload(), 1000);
        },
        error: (error) => {
          console.error('Activation error:', error);
          const errorMessage = error.error?.message || 'Failed to activate driver!';
          this.dialogService.open('Error', errorMessage, true);
        }
      });
  }

  onDeactivateClick() {
    console.log('Deactivate clicked');
    this.driverService.deactivateDriver().subscribe({
      next: () => {
        this.dialogService.open('Success', 'Driver deactivated successfully!', false);
        this.isDriverActive.set(false);
        this.profile.isActive = false;
        setTimeout(() => window.location.reload(), 1000);
      },
      error: (error) => {
        console.error('Deactivation error:', error);
        const errorMessage = error.error?.message || 'Failed to deactivate driver!';
        this.dialogService.open('Error', errorMessage, true);
      }
    });
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

  ngOnDestroy() {
    this.driverStatusSubscription?.unsubscribe();
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
    const editRequest: EditDriverProfileRequestDTO = MapProfileToEditDriverProfileRequestDTO(this.updatedProfile);
    console.log('Edit request being sent:', editRequest);
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

  getTimeActive(): string {
    return this.timeActive();
  }

  private fetchActiveTime() {
    console.log('Fetching active time for driver...');
    this.driverService.getDriverActiveLast24Hours().subscribe({
      next: (response) => {
        console.log('Active time response:', response);
        this.timeActive.set(this.formatDuration(response.timeActive));
      },
      error: (error) => {
        console.error('Error fetching active time:', error);
        this.timeActive.set('N/A');
      }
    });
  }

  private formatDuration(duration: string): string {
    // Duration format from Java is like "PT2H30M15S" (ISO 8601)
    const regex = /PT(?:(\d+)H)?(?:(\d+)M)?(?:(\d+(?:\.\d+)?)S)?/;
    const match = duration.match(regex);
    
    if (!match) return '0h 0m';
    
    const hours = parseInt(match[1] || '0');
    const minutes = parseInt(match[2] || '0');
    const seconds = Math.floor(parseFloat(match[3] || '0'));
    
    if (hours > 0) {
      return `${hours}h ${minutes}m`;
    } else if (minutes > 0) {
      return `${minutes}m ${seconds}s`;
    } else {
      return `${seconds}s`;
    }
  }
}
