import { Component, input, InputSignal, signal, computed, output } from '@angular/core';
import { DriverUpdateRequestRow } from '../driver-update-request-row/driver-update-request-row';
import { DriverUpdateRequestDiffDTO, EditDriverProfileRequestDTO } from '@app/shared/models/user/editProfileDTO';

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

  approve = output<number>();
  reject = output<number>();

  toggleExpand() {
    this.isExpanded.set(!this.isExpanded());
  }

  request: InputSignal<DriverUpdateRequestDiffDTO> = input.required<DriverUpdateRequestDiffDTO>();

  // Mapping of human-readable labels to value pickers from the DTO
  private readonly fieldMappings: { label: string; pick: (d: EditDriverProfileRequestDTO | undefined) => unknown }[] = [
    // Profile (basic user info)
    { label: 'Name', pick: (d) => d?.profile.name },
    { label: 'Surname', pick: (d) => d?.profile.surname },
    { label: 'Phone Number', pick: (d) => d?.profile.phoneNumber },
    { label: 'Address', pick: (d) => d?.profile.address },
    // Vehicle details
    { label: 'Vehicle Make', pick: (d) => d?.vehicleMake },
    { label: 'Vehicle Model', pick: (d) => d?.vehicleModel },
    { label: 'Vehicle Color', pick: (d) => d?.vehicleColor },
    { label: 'License Plate', pick: (d) => d?.vehicleLicensePlate },
    { label: 'Seats', pick: (d) => d?.vehicleSeats },
    { label: 'Pet Friendly', pick: (d) => d?.vehiclePetFriendly },
    { label: 'Baby Friendly', pick: (d) => d?.vehicleBabyFriendly },
    { label: 'Vehicle Type', pick: (d) => d?.vehicleType },
  ];

  changedFields = computed(() => {
    const changes: ChangedField[] = [];
    const req = this.request();

    const normalize = (value: unknown): string => {
      if (value === undefined || value === null) return '';
      if (typeof value === 'boolean') return value ? 'Yes' : 'No';
      return String(value).trim();
    };

    this.fieldMappings.forEach(({ label, pick }) => {
      const oldValue = normalize(pick(req.oldData));
      const newValue = normalize(pick(req.newData));

      if (oldValue !== newValue) {
        changes.push({
          label,
          oldValue: oldValue || 'Not set',
          newValue: newValue || 'Not set',
        });
      }
    });

    return changes;
  });

  getDriverName(): string {
    const req = this.request();
    const profile = req.oldData?.profile ?? req.newData?.profile;
    const name = profile?.name ?? '';
    const surname = profile?.surname ?? '';
    return `${name} ${surname}`.trim();
  }

  getDriverEmail(): string {
    return this.request().email;
  }

  approveEditRequest() {
    this.approve.emit(this.request().requestId);
  }

  rejectEditRequest() {
    this.reject.emit(this.request().requestId);
  }
}
