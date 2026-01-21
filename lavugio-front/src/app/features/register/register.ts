import { Component, signal, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Navbar } from '@app/shared/components/navbar/navbar';
import { AuthService, RegistrationRequest } from '@app/core/services/auth-service';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-register',
  imports: [FormsModule, Navbar, CommonModule],
  templateUrl: './register.html',
  styleUrl: './register.css',
})
export class Register {
  private authService = inject(AuthService);
  private router = inject(Router);

  email = signal('');
  password = signal('');
  name = signal('');
  surname = signal('');
  address = signal('');
  phoneNumber = signal('');
  showPassword = signal(false);
  loading = signal(false);
  errorMessage = signal('');
  successMessage = signal('');

  togglePasswordVisibility() {
    this.showPassword.update(val => !val);
  }

  handleRegister() {
    // Clear previous messages
    this.errorMessage.set('');
    this.successMessage.set('');

    // Validate form
    if (!this.validateForm()) {
      return;
    }

    this.loading.set(true);

    const registrationData: RegistrationRequest = {
      email: this.email(),
      password: this.password(),
      name: this.name(),
      lastName: this.surname(),
      phoneNumber: this.phoneNumber(),
      address: this.address(),
    };

    this.authService.register(registrationData).subscribe({
      next: (response) => {
        this.loading.set(false);
        this.successMessage.set('Registration successful! Redirecting to login...');
        setTimeout(() => {
          this.router.navigate(['/login']);
        }, 2000);
      },
      error: (error) => {
        this.loading.set(false);
        const errorMsg = error.error?.message || error.message || 'Registration failed. Please try again.';
        this.errorMessage.set(errorMsg);
        console.error('Registration error:', error);
      },
    });
  }

  private validateForm(): boolean {
    if (!this.email()) {
      this.errorMessage.set('Email is required');
      return false;
    }
    if (!this.password()) {
      this.errorMessage.set('Password is required');
      return false;
    }
    if (this.password().length < 8) {
      this.errorMessage.set('Password must be at least 8 characters');
      return false;
    }
    if (!this.name()) {
      this.errorMessage.set('Name is required');
      return false;
    }
    if (!this.surname()) {
      this.errorMessage.set('Surname is required');
      return false;
    }
    if (!this.address()) {
      this.errorMessage.set('Address is required');
      return false;
    }
    if (!this.phoneNumber()) {
      this.errorMessage.set('Phone number is required');
      return false;
    }
    return true;
  }
}
