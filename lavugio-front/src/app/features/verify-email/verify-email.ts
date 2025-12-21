import { Component, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Navbar } from '@app/layout/navbar/navbar';

@Component({
  selector: 'app-verify-email',
  imports: [FormsModule, Navbar],
  templateUrl: './verify-email.html',
  styleUrl: './verify-email.css',
})
export class VerifyEmail {
  code = signal('');

  handleConfirm() {
    console.log('Email verification code:', this.code());
  }
}
