import { Component, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Navbar } from '@app/components/navbar/navbar';

@Component({
  selector: 'app-login',
  imports: [FormsModule, Navbar],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login {
  email = signal('');
  password = signal('');
  showPassword = signal(false);

  togglePasswordVisibility() {
    this.showPassword.update(val => !val);
  }

  handleLogin() {
    console.log('Login attempt:', {
      email: this.email(),
      password: this.password()
    });
  }
}
