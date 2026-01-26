import { Component, signal, inject, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Navbar } from '@app/shared/components/navbar/navbar';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '@app/core/services/auth-service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-verify-email',
  imports: [FormsModule, Navbar, CommonModule],
  templateUrl: './verify-email.html',
  styleUrl: './verify-email.css',
})
export class VerifyEmail implements OnInit {
  private activatedRoute = inject(ActivatedRoute);
  private authService = inject(AuthService);
  private router = inject(Router);

  loading = signal(false);
  verified = signal(false);
  errorMessage = signal('');
  successMessage = signal('');

  ngOnInit() {
    // Get token from URL query parameter
    this.activatedRoute.queryParams.subscribe(params => {
      const token = params['token'];
      if (token) {
        this.verifyEmail(token);
      } else {
        this.errorMessage.set('No verification token provided');
      }
    });
  }

  private verifyEmail(token: string) {
    this.loading.set(true);

    this.authService.verifyEmail(token).subscribe({
      next: (response) => {
        this.loading.set(false);
        this.verified.set(true);
        this.successMessage.set('✓ Email verified successfully! Redirecting to login...');
        setTimeout(() => {
          this.router.navigate(['/login']);
        }, 2000);
      },
      error: (error) => {
        this.loading.set(false);
        
        // Handle different error types
        if (error.error && error.error.message) {
          this.errorMessage.set(error.error.message);
        } else if (error.error && typeof error.error === 'string') {
          this.errorMessage.set(error.error);
        } else if (error.status === 400) {
          this.errorMessage.set('Invalid or expired verification token. Please register again.');
        } else {
          this.errorMessage.set('Email verification failed. Please try again.');
        }
        
        console.error('Verification error:', error);
      },
    });
  }
}

