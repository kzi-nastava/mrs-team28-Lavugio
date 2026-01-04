import { Component, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Navbar } from '@app/components/navbar/navbar';

@Component({
  selector: 'app-register',
  imports: [FormsModule, Navbar],
  templateUrl: './register.html',
  styleUrl: './register.css',
})
export class Register {
  email = signal('');
  password = signal('');
  name = signal('');
  surname = signal('');
  address = signal('');
  phoneNumber = signal('');
  showPassword = signal(false);

  togglePasswordVisibility() {
    this.showPassword.update(val => !val);
  }

  handleRegister() {
    console.log('Register attempt:', {
      email: this.email(),
      password: this.password(),
      name: this.name(),
      surname: this.surname(),
      address: this.address(),
      phoneNumber: this.phoneNumber()
    });
  }
}
