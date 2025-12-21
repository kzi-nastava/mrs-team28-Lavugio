import { Component, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Navbar } from '@app/layout/navbar/navbar';

@Component({
  selector: 'app-forgot-password',
  imports: [FormsModule, Navbar],
  templateUrl: './forgot-password.html',
  styleUrl: './forgot-password.css',
})
export class ForgotPassword {
  email = signal('');

  handleSendLink() {
    console.log('Reset link sent to:', this.email());
  }
}
