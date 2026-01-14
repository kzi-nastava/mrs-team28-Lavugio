import { Component, effect, EventEmitter, Output, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-vehicle-form',
  imports: [FormsModule],
  templateUrl: './vehicle-form.html',
  styleUrl: './vehicle-form.css',
})
export class VehicleForm {
  @Output() dataChange = new EventEmitter<any>();

  make = signal('');
  model = signal('');
  licensePlate = signal('');
  seats = signal(4);
  color = signal('');

  vehicleTypes = ['Standard', 'Luxury', 'Combi'];
  selectedType = signal('Standard');

  isPetFriendly = signal(false);
  isBabyFriendly = signal(false);

  constructor() {
    effect (() => {
      this.dataChange.emit({
        make: this.make(),
        model: this.model(),
        licensePlate: this.licensePlate(),
        seats: this.seats(),
        color: this.color(),
        vehicleType: this.selectedType(),
        isPetFriendly: this.isPetFriendly(),
        isBabyFriendly: this.isBabyFriendly()
      });
    });
  }
}
