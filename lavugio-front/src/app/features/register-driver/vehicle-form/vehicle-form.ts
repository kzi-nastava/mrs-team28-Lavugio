import { Component, computed, effect, EventEmitter, Input, Output, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-vehicle-form',
  imports: [FormsModule],
  templateUrl: './vehicle-form.html',
  styleUrl: './vehicle-form.css',
})
export class VehicleForm {
  @Input() set initialData(data: any) {
    if (data) {
      this.make.set(data.make || '');
      this.model.set(data.model || '');
      this.licenseNumber.set(data.licenseNumber || '');
      this.licensePlate.set(data.licensePlate || '');
      this.seats.set(data.seats || 4);
      this.color.set(data.color || '');
      this.selectedType.set(data.vehicleType || 'Standard');
      this.isPetFriendly.set(data.petFriendly || false);
      this.isBabyFriendly.set(data.babyFriendly || false);
    }
  }

  @Output() dataChange = new EventEmitter<any>();

  make = signal('');
  model = signal('');
  licenseNumber = signal('');
  licensePlate = signal('');
  seats = signal(4);
  color = signal('');

  vehicleTypes = ['Standard', 'Luxury', 'Combi'];
  selectedType = signal('Standard');

  isPetFriendly = signal(false);
  isBabyFriendly = signal(false);

  submitted = signal(false);

  private licensePlateRegex = /^[A-Z]{2}-\d{3,5}-[A-Z]{2}$/;
  isLicensePlateValid = computed(() => {
    return this.licensePlateRegex.test(this.licensePlate());
  });

  constructor() {
    effect(() => {
      this.make();
      this.model();
      this.licenseNumber();
      this.licensePlate();
      this.seats();
      this.color();
      this.selectedType();
      this.isPetFriendly();
      this.isBabyFriendly();
      this.emitData();
    });
  }

  emitData() {
    this.dataChange.emit({
      make: this.make(),
      model: this.model(),
      licenseNumber: this.licenseNumber(),
      licensePlate: this.licensePlate(),
      seats: this.seats(),
      color: this.color(),
      vehicleType: this.selectedType(),
      petFriendly: this.isPetFriendly(),
      babyFriendly: this.isBabyFriendly(),
    });
  }

  onSubmit() {
    this.submitted.set(true);
  }

  isFormValid(): boolean {
    return !!this.make() && !!this.model() && this.isLicensePlateValid() && 
           this.seats() > 0 && !!this.color() && !!this.selectedType();
  }
}
