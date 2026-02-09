import { Component, computed, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { DialogService } from '@app/core/services/dialog-service';
import { UserService } from '@app/core/services/user/user-service';
import { Navbar } from '@app/shared/components/navbar/navbar';
import { LiveSupportButtonComponent } from "@app/shared/components/live-support-button/live-support-button";

@Component({
  selector: 'app-driver-activation',
  imports: [FormsModule, Navbar, LiveSupportButtonComponent],
  templateUrl: './driver-activation.html',
  styleUrl: './driver-activation.css',
})
export class DriverActivation {
  constructor(
    private userService: UserService,
    private router: Router,
    private route: ActivatedRoute,
    private dialogService: DialogService
  ) {}
  
  token = signal<string | null>(null);

  password = signal('');
  passwordAgain = signal('');
  
  isLoading = signal(false);
  isValidatingToken = signal(true);
  
  isPasswordTheSame = computed(() => {
    const pass = this.password();
    const passAgain = this.passwordAgain();

    return pass !== '' && passAgain !== '' && pass === passAgain && !this.isLoading() && this.token();
  });

  ngOnInit() {
    this.route.queryParams.subscribe(params => {
      const tokenFromUrl = params['token'];

      console.log("Activation token from URL:", tokenFromUrl);

      if (!tokenFromUrl) {
        this.dialogService.open("Invalid link", "Activation link is missing token. Please check your email and try again.", true);
        this.router.navigate(['/']);
        return;
      }
      this.token.set(tokenFromUrl);
      this.validateToken(tokenFromUrl);
    });
  }

  validateToken(token: string) {
    this.isValidatingToken.set(true);
    this.userService.validateActivationToken(token);

  }

  activateAccount() {
    if (!this.isPasswordTheSame()) {
      return;
    }

    const currentToken = this.token();
    if (!currentToken) {
      return;
    }

    this.isLoading.set(true);
    this.userService.activateAccount(currentToken, this.password()).subscribe({
      next: (response) => {
        console.log('Account activated successfully:', response);
        this.isLoading.set(false);
        
        this.dialogService.open(
          'Account Activated!',
          'Your account has been successfully activated. You can now log in.',
          false
        );
        
        setTimeout(() => {
          this.router.navigate(['/login']);
        }, 200);
      },
      error: (error) => {
        console.error('Activation failed:', error);
        console.log(error.message);
        this.dialogService.open(
          'Activation Failed',
          error.error?.message || 'There was an error activating your account. Please try again.',
          true
        );
        
        this.isLoading.set(false);
      }
    });
  }
}
