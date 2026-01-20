import { Component, signal, ViewChild } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { Navbar } from '@app/shared/components/navbar/navbar';
import { DriverForm } from './driver-form/driver-form';
import { VehicleForm } from './vehicle-form/vehicle-form';
import { DialogService } from '@app/core/services/dialog-service';
import { DriverService } from '@app/core/services/driver-service';

@Component({
  selector: 'app-register-driver',
  imports: [Navbar, FormsModule, DriverForm, VehicleForm],
  templateUrl: './register-driver.html',
  styleUrl: './register-driver.css',
})
export class RegisterDriver {
  @ViewChild(DriverForm) driverForm!: DriverForm;

  currentStep = 0;

  driverData: any = {};
  vehicleData: any = {};

  constructor(
    private driverService: DriverService,
    private router: Router,
    private dialogService: DialogService
  ) {}

  nextStep() {
    // Validate current step before proceeding
    if (this.currentStep === 0 && this.driverForm) {
      this.driverForm.onSubmit();
      // Check if driver form is valid
      if (!this.driverForm.isFormValid()) {
        return;
      }
    }
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
    const fullData = {
      ...this.driverData,
      ...this.vehicleData
    };

    if (fullData.vehicleType == 'Combi') {
      fullData.vehicleType = 'COMBI';
    } else if (fullData.vehicleType == 'Luxury') {
      fullData.vehicleType = 'LUXURY';
    } else {
      fullData.vehicleType = 'STANDARD';
    }

    console.log('Registering driver with data:', fullData);

    this.driverService.registerDriver(fullData).subscribe({
      next: (res) => {
        this.dialogService.open('Registration Successful', 'Your driver account has been created successfully!', false);
        // Redirect to another page after successful registration
        // Example: redirect to home/dashboard page after 2 seconds
        setTimeout(() => {
          this.router.navigate(['/']); // Change '/home' to your desired route
        }, 2000);
      },
      error: (err) => {
        console.log(err.error);
        this.dialogService.open('Registration Failed', err.error.message || 'An error occurred during registration. Please try again.', true);
      },
    });
  }

  isFirstStep() {
    return this.currentStep === 0;
  }

  isLastStep() {
    return this.currentStep === 1;
  }
}
