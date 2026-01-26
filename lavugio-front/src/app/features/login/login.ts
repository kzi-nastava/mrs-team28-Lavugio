import { Component, signal, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Navbar } from '@app/shared/components/navbar/navbar';
import { AuthService, LoginRequest } from '@app/core/services/auth-service';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-login',
  imports: [FormsModule, Navbar, CommonModule],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login {
  private authService = inject(AuthService);
  private router = inject(Router);

  email = signal('');
  password = signal('');
  showPassword = signal(false);
  loading = signal(false);
  errorMessage = signal('');
  successMessage = signal('');

  togglePasswordVisibility() {
    this.showPassword.update(val => !val);
  }

  redirectBasedOnRole(role: string) {
    switch (role) {
      case 'DRIVER':
        this.router.navigate(['/driver-scheduled-rides']);
        break;
      case 'ADMINISTRATOR':
        this.router.navigate(['/admin-panel']);
        break;
      case 'REGULAR_USER':
      default:
        this.router.navigate(['/find-trip']);
        break;
    }
  }

  handleLogin() {
    // Clear previous messages
    this.errorMessage.set('');
    this.successMessage.set('');

    // Validate form
    if (!this.email()) {
      this.errorMessage.set('Email is required');
      return;
    }
    if (!this.password()) {
      this.errorMessage.set('Password is required');
      return;
    }

    this.loading.set(true);

    const loginData: LoginRequest = {
      email: this.email(),
      password: this.password(),
    };

    this.authService.login(loginData).subscribe({
      next: (response) => {
        this.loading.set(false);
        // Store token and user data
        this.authService.storeToken(response.token, response);
        this.successMessage.set('Login successful! Redirecting...');
        
        // Redirect based on user role
        setTimeout(() => {
          this.redirectBasedOnRole(response.role);
        }, 1000);
      },
      error: (error) => {
        this.loading.set(false);
        
        // Handle different error types
        if (error.error && error.error.message) {
          this.errorMessage.set(error.error.message);
        } else if (error.error && typeof error.error === 'string') {
          this.errorMessage.set(error.error);
        } else if (error.status === 401) {
          this.errorMessage.set('Invalid email or password');
        } else if (error.status === 404) {
          this.errorMessage.set('Email not found');
        } else {
          this.errorMessage.set('Login failed. Please try again.');
        }
        
        console.error('Login error:', error);
      },
    });
  }
}

