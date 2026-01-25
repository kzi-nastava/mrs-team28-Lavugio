import { Component, input, InputSignal, signal, computed } from '@angular/core';
import { DriverUpdateRequestRow } from '../driver-update-request-row/driver-update-request-row';
import { UserProfile } from '@app/shared/models/user/userProfile';

interface ChangedField {
  label: string;
  oldValue: string;
  newValue: string;
}

@Component({
  selector: 'app-driver-update-request',
  imports: [DriverUpdateRequestRow],
  templateUrl: './driver-update-request.html',
  styleUrl: './driver-update-request.css',
})
export class DriverUpdateRequest {

  isExpanded = signal<boolean>(false);

  toggleExpand() {
    this.isExpanded.set(!this.isExpanded());
  }

  oldProfile: InputSignal<UserProfile> = input.required<UserProfile>();
  newProfile: InputSignal<UserProfile> = input.required<UserProfile>();

  changedFields = computed(() => {
    const changes: ChangedField[] = [];
    const old = this.oldProfile();
    const newP = this.newProfile();

    const toString = (value: any): string => {
      if (value === undefined || value === null) return '';
      if (typeof value === 'boolean') return value ? 'Yes' : 'No';
      return String(value);
    };

    const fieldMappings: { key: keyof UserProfile; label: string }[] = [
      { key: 'name', label: 'Name' },
      { key: 'surname', label: 'Surname' },
      { key: 'phoneNumber', label: 'Phone Number' },
      { key: 'email', label: 'Email' },
      { key: 'address', label: 'Address' },
      { key: 'vehicleMake', label: 'Vehicle Make' },
      { key: 'vehicleModel', label: 'Vehicle Model' },
      { key: 'vehicleColor', label: 'Vehicle Color' },
      { key: 'vehicleLicensePlate', label: 'License Plate' },
      { key: 'vehicleSeats', label: 'Seats' },
      { key: 'vehiclePetFriendly', label: 'Pet Friendly' },
      { key: 'vehicleBabyFriendly', label: 'Baby Friendly' },
      { key: 'vehicleType', label: 'Vehicle Type' },
    ];

    fieldMappings.forEach(({ key, label }) => {
      const oldValue = toString(old[key]);
      const newValue = toString(newP[key]);
      
      if (oldValue !== newValue) {
        changes.push({
          label,
          oldValue: oldValue || 'Not set',
          newValue: newValue || 'Not set'
        });
      }
    });

    return changes;
  });

  getDriverName(): string {
    const profile = this.oldProfile();
    return `${profile.name} ${profile.surname}`;
  }

  getDriverEmail(): string {
    return this.oldProfile().email;
  }

  approveEditRequest() {
    alert('Edit request approved (functionality not implemented).');
  }

  cancelEditRequest() {
    alert('Edit request canceled (functionality not implemented).');
  }
}
