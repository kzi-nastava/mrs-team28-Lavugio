import { Component, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Navbar } from '@app/components/navbar/navbar';

@Component({
  selector: 'app-reset-password',
  imports: [FormsModule, Navbar],
  templateUrl: './reset-password.html',
  styleUrl: './reset-password.css',
})
export class ResetPassword {
  password = signal('');
  confirmPassword = signal('');
  showPassword = signal(false);
  showConfirmPassword = signal(false);

  togglePasswordVisibility() {
    this.showPassword.update(val => !val);
  }

  toggleConfirmPasswordVisibility() {
    this.showConfirmPassword.update(val => !val);
  }

  handleResetPassword() {
    if (this.password() === this.confirmPassword()) {
      console.log('Password reset with:', this.password());
    } else {
      console.log('Passwords do not match');
    }
  }
}
