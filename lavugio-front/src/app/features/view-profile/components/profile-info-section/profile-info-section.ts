import { Component, computed, inject, signal } from '@angular/core';
import { ProfileInfoRow } from '../profile-info-row/profile-info-row';
import { Button } from './../../../../shared/components/button/button';
import { ProfileEdit } from '../../services/profile-edit';

interface UserProfile {
  name: string;
  surname: string;
  phoneNumber: string;
  email: string;
  address: string;
  isDriver: boolean;
  vehicle?: string;
  vehicleType?: string;
  activeTime?: string;
}

@Component({
  selector: 'app-profile-info-section',
  imports: [ProfileInfoRow, Button],
  templateUrl: './profile-info-section.html',
  styleUrl: './profile-info-section.css',
})

export class ProfileInfoSection {
  editService = inject(ProfileEdit);
  
  userProfile = signal<UserProfile>({
    name: 'Lazar',
    surname: 'Jović',
    phoneNumber: '069123456',
    email: 'bm230294d@student.etf.bg.ac.rs',
    address: 'Beogradska 35, Požarevac 12000',
    isDriver: true,
    vehicle: 'Smart Crossblade',
    vehicleType: 'Standard',
    activeTime: '4h30min',
  });

  editButtonText = computed(() => {
    if (!this.editService.isEditMode()) {
      return 'Edit';
    }
    return this.userProfile().isDriver ? 'Send Request' : 'Save';
  });

  onFieldChanged(field: keyof UserProfile, value: string) {
    this.userProfile.update((profile) => ({
      ...profile,
      [field]: value,
    }));
  }

  onEditClick() {
    if (this.editService.isEditMode()) {
      if (this.userProfile().isDriver) {
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

  private saveProfile() {
    console.log('Saving profile:', this.userProfile());
    // API call za čuvanje
    // this.profileService.updateProfile(this.userProfile()).subscribe(...)

    // Nakon uspešnog čuvanja, isključi edit mode
    this.editService.disableEditMode();
  }

  private sendEditRequest() {
    console.log('Sending edit request:', this.userProfile());
    // API call za slanje requesta adminu
    // this.profileService.sendEditRequest(this.userProfile()).subscribe(...)

    // Nakon uspešnog slanja, isključi edit mode
    this.editService.disableEditMode();
  }
}
