import { Component, signal, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Navbar } from '@app/shared/components/navbar/navbar';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { environment } from '@environments/environment';

@Component({
  selector: 'app-forgot-password',
  imports: [FormsModule, Navbar],
  templateUrl: './forgot-password.html',
  styleUrl: './forgot-password.css',
})
export class ForgotPassword {
  private http = inject(HttpClient);
  private router = inject(Router);
  
  email = signal('');
  loading = signal(false);
  message = signal('');
  error = signal('');

  handleSendLink() {
    this.loading.set(true);
    this.message.set('');
    this.error.set('');
    
    this.http.post(`${environment.BACKEND_URL}/api/regularUsers/forgot-password`, {
      email: this.email()
    }).subscribe({
      next: (response: any) => {
        this.loading.set(false);
        this.message.set(response.message || 'If an account with that email exists, a password reset link has been sent.');
      },
      error: (error) => {
        this.loading.set(false);
        this.error.set(error.error?.error || 'An error occurred. Please try again.');
      }
    });
  }
}
