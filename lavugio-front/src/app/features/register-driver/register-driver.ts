import { Component, signal, ViewChild } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { Navbar } from '@app/shared/components/navbar/navbar';
import { DriverForm } from './driver-form/driver-form';
import { VehicleForm } from './vehicle-form/vehicle-form';
import { DriverService } from '@app/core/services/user/driver-service';
import { DriverRegistration } from '@app/shared/models/user/driverRegistration';
import { DialogService } from '@app/core/services/dialog-service';

@Component({
  selector: 'app-register-driver',
  imports: [Navbar, FormsModule, DriverForm, VehicleForm],
  templateUrl: './register-driver.html',
  styleUrl: './register-driver.css',
})
export class RegisterDriver {
  @ViewChild(DriverForm) driverForm!: DriverForm;
  @ViewChild(VehicleForm) vehicleForm!: VehicleForm;

  currentStep = 0;

  driverData: any = {};
  vehicleData: any = {};

  constructor(private driverService: DriverService,
              private dialogService: DialogService,
              private router: Router  
  ) {}

  onDriverDataChange(data: any) {
    this.driverData = data;
  }

  onVehicleDataChange(data: any) {
    this.vehicleData = data;
  }

  finishRegistration() {
    // Trigger validation on the current form
    if (this.currentStep === 0 && this.driverForm) {
      this.driverForm.onSubmit();
      // Check if driver form is valid
      if (!this.driverForm.isFormValid()) {
        return;
      }
    } else if (this.currentStep === 1 && this.vehicleForm) {
      this.vehicleForm.onSubmit();
      // Check if vehicle form is valid
      if (!this.vehicleForm.isFormValid()) {
        return;
      }
      
      // Proceed with registration
      this.performRegistration();
    }
  }

  private performRegistration() {
    const fullData: DriverRegistration = {
      email: this.driverData.email,
      password: this.driverData.password,
      name: this.driverData.name,
      lastName: this.driverData.surname,
      phoneNumber: this.driverData.phoneNumber,
      address: this.driverData.address,

      vehicleMake: this.vehicleData.make,
      vehicleModel: this.vehicleData.model,
      licensePlate: this.vehicleData.licensePlate,
      vehicleColor: this.vehicleData.color,
      vehicleType: this.vehicleData.vehicleType,

      passangerSeats: this.vehicleData.seats,

      petFriendly: this.vehicleData.petFriendly,
      babyFriendly: this.vehicleData.babyFriendly,
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

  nextStep() {
    // Validate current step before proceeding
    if (this.currentStep === 0 && this.driverForm) {
      this.driverForm.onSubmit();
      // Check if driver form is valid
      if (!this.driverForm.isFormValid()) {
        return;
      }
    }
    this.currentStep++;
  }

  previousStep() {
    this.currentStep--;
  }

  isFirstStep() {
    return this.currentStep === 0;
  }

  isLastStep() {
    return this.currentStep === 1;
  }
}
