import { Component, EventEmitter, Input, Output, OnInit } from '@angular/core';
import { VehicleSelect } from "../vehicle-select/vehicle-select";
import { AddPassangerInput, Passenger } from "../add-passanger-input/add-passanger-input";
import { PassangersDisplay } from "../passangers-display/passangers-display";

@Component({
  selector: 'app-preferences-select',
  imports: [VehicleSelect, AddPassangerInput, PassangersDisplay],
  templateUrl: './preferences-select.html',
  styleUrl: './preferences-select.css',
})
export class PreferencesSelect implements OnInit {
  @Input() passengers: Passenger[] = [];
  @Input() initialVehicleType: string = '';
  @Input() initialIsPetFriendly: boolean = false;
  @Input() initialIsBabyFriendly: boolean = false;
  @Output() preferencesChanged = new EventEmitter<{vehicleType: string, isPetFriendly: boolean, isBabyFriendly: boolean}>();
  @Output() passengerAdded = new EventEmitter<Passenger>();
  @Output() passengerRemoved = new EventEmitter<string>();
  @Output() petFriendlyChanged = new EventEmitter<boolean>();
  @Output() babyFriendlyChanged = new EventEmitter<boolean>();
  @Output() vehicleTypeChanged = new EventEmitter<string>();

  selectedVehicleType: string = '';
  isPetFriendly = false;
  isBabyFriendly = false;

  ngOnInit() {
    // Load initial values from inputs
    this.selectedVehicleType = this.initialVehicleType;
    this.isPetFriendly = this.initialIsPetFriendly;
    this.isBabyFriendly = this.initialIsBabyFriendly;
  }

  onPassengerAdded(passenger: Passenger) {
    console.log('PreferencesSelect - onPassengerAdded:', passenger);
    this.passengerAdded.emit(passenger);
  }

  onPassengerRemoved(passengerId: string) {
    console.log('PreferencesSelect - onPassengerRemoved:', passengerId);
    this.passengerRemoved.emit(passengerId);
  }

  onVehicleSelected(vehicleType: string) {
    this.selectedVehicleType = vehicleType;
    this.vehicleTypeChanged.emit(vehicleType);
    this.emitPreferencesChange();
  }

  onPetFriendlyChange(value: boolean) {
    this.isPetFriendly = value;
    this.petFriendlyChanged.emit(value);
    this.emitPreferencesChange();
  }

  onBabyFriendlyChange(value: boolean) {
    this.isBabyFriendly = value;
    this.babyFriendlyChanged.emit(value);
    this.emitPreferencesChange();
  }

  private emitPreferencesChange() {
    this.preferencesChanged.emit({
      vehicleType: this.selectedVehicleType,
      isPetFriendly: this.isPetFriendly,
      isBabyFriendly: this.isBabyFriendly
    });
  }
}
