import { Component, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Navbar } from '@app/shared/components/navbar/navbar';
import { DriverForm } from './driver-form/driver-form';
import { VehicleForm } from './vehicle-form/vehicle-form';

@Component({
  selector: 'app-register-driver',
  imports: [Navbar, FormsModule, DriverForm, VehicleForm],
  templateUrl: './register-driver.html',
  styleUrl: './register-driver.css',
})
export class RegisterDriver {
  currentStep = 0;

  driverData: any = {};
  vehicleData: any = {};

  nextStep() {
    if (this.currentStep < 1) {
      this.currentStep++;
    }
  }

  previousStep() {
    if (this.currentStep > 0) {
      this.currentStep--;
    }
  }

  onDriverDataChange(data: any) {
    this.driverData = data;
  }

  onVehicleDataChange(data: any) {
    this.vehicleData = data;
  }

  finishRegistration() {
    const finalPayload = {
      driver: this.driverData,
      vehicle: this.vehicleData
    };

    console.log("Slanje na backend:", finalPayload);
    // Call for backend endpoint to register driver
  }

  isFirstStep() {
    return this.currentStep === 0;
  }

  isLastStep() {
    return this.currentStep === 1;
  }
}
