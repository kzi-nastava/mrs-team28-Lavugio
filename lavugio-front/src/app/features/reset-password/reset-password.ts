import { Component, signal, inject, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Navbar } from '@app/shared/components/navbar/navbar';
import { HttpClient } from '@angular/common/http';
import { Router, ActivatedRoute } from '@angular/router';
import { environment } from '@environments/environment';

@Component({
  selector: 'app-reset-password',
  imports: [FormsModule, Navbar],
  templateUrl: './reset-password.html',
  styleUrl: './reset-password.css',
})
export class ResetPassword implements OnInit {
  private http = inject(HttpClient);
  private router = inject(Router);
  private route = inject(ActivatedRoute);
  
  password = signal('');
  confirmPassword = signal('');
  showPassword = signal(false);
  showConfirmPassword = signal(false);
  loading = signal(false);
  message = signal('');
  error = signal('');
  token = signal('');

  ngOnInit() {
    // Get token from URL query params
    this.route.queryParams.subscribe(params => {
      const token = params['token'];
      if (token) {
        this.token.set(token);
      } else {
        this.error.set('Invalid reset link. Please request a new password reset.');
      }
    });
  }

  togglePasswordVisibility() {
    this.showPassword.update(val => !val);
  }

  toggleConfirmPasswordVisibility() {
    this.showConfirmPassword.update(val => !val);
  }

  handleResetPassword() {
    this.error.set('');
    this.message.set('');
    
    if (this.password() !== this.confirmPassword()) {
      this.error.set('Passwords do not match');
      return;
    }
    
    if (this.password().length < 6) {
      this.error.set('Password must be at least 6 characters long');
      return;
    }
    
    if (!this.token()) {
      this.error.set('Invalid reset token');
      return;
    }
    
    this.loading.set(true);
    
    this.http.post(`${environment.BACKEND_URL}/api/regularUsers/reset-password`, {
      token: this.token(),
      newPassword: this.password()
    }).subscribe({
      next: (response: any) => {
        this.loading.set(false);
        this.message.set(response.message || 'Password successfully reset');
        // Redirect to login after 2 seconds
        setTimeout(() => {
          this.router.navigate(['/login']);
        }, 2000);
      },
      error: (error) => {
        this.loading.set(false);
        this.error.set(error.error?.error || 'An error occurred. Please try again.');
      }
    });
  }
}
