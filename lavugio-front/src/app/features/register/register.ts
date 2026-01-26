import { Component, signal, inject, ViewChild, ElementRef } from '@angular/core';
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
  @ViewChild('fileInput') fileInput!: ElementRef<HTMLInputElement>;

  private authService = inject(AuthService);
  private router = inject(Router);

  email = signal('');
  password = signal('');
  confirmPassword = signal('');
  name = signal('');
  surname = signal('');
  address = signal('');
  phoneNumber = signal('');
  showPassword = signal(false);
  showConfirmPassword = signal(false);
  loading = signal(false);
  errorMessage = signal('');
  successMessage = signal('');
  profilePicture = signal<File | null>(null);
  profilePicturePreview = signal<string | null>(null);

  togglePasswordVisibility() {
    this.showPassword.update(val => !val);
  }

  toggleConfirmPasswordVisibility() {
    this.showConfirmPassword.update(val => !val);
  }

  onProfilePictureSelected(event: any) {
    const file = event.target.files[0];
    if (file) {
      // Validate file type
      if (!file.type.startsWith('image/')) {
        this.errorMessage.set('Please select a valid image file');
        return;
      }

      // Validate file size (max 5MB)
      if (file.size > 5 * 1024 * 1024) {
        this.errorMessage.set('Image size must be less than 5MB');
        return;
      }

      this.profilePicture.set(file);

      // Create preview
      const reader = new FileReader();
      reader.onload = (e: any) => {
        this.profilePicturePreview.set(e.target.result);
      };
      reader.readAsDataURL(file);
    }
  }

  triggerFileInput() {
    this.fileInput.nativeElement.click();
  }

  removeProfilePicture() {
    this.profilePicture.set(null);
    this.profilePicturePreview.set(null);
    if (this.fileInput) {
      this.fileInput.nativeElement.value = '';
    }
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

    const formData = new FormData();
    formData.append('email', this.email());
    formData.append('password', this.password());
    formData.append('name', this.name());
    formData.append('lastName', this.surname());
    formData.append('phoneNumber', this.phoneNumber());
    formData.append('address', this.address());

    if (this.profilePicture()) {
      formData.append('profilePicture', this.profilePicture()!);
    }

    this.authService.registerWithFile(formData).subscribe({
      next: (response) => {
        this.loading.set(false);
        this.successMessage.set('Registration successful! Check your email for verification link. Redirecting to login...');
        setTimeout(() => {
          this.router.navigate(['/login']);
        }, 3000);
      },
      error: (error) => {
        this.loading.set(false);
        
        // Handle different error types
        if (error.error && error.error.fieldErrors) {
          const fieldErrors = error.error.fieldErrors;
          const errorMessages = Object.entries(fieldErrors)
            .map(([field, message]) => `${field}: ${message}`)
            .join('\n');
          this.errorMessage.set(errorMessages);
        } else if (error.error && error.error.message) {
          this.errorMessage.set(error.error.message);
        } else if (error.error && typeof error.error === 'string') {
          this.errorMessage.set(error.error);
        } else {
          this.errorMessage.set('Registration failed. Please try again.');
        }
        
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
    if (!this.confirmPassword()) {
      this.errorMessage.set('Please confirm your password');
      return false;
    }
    if (this.password() !== this.confirmPassword()) {
      this.errorMessage.set('Passwords do not match');
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
