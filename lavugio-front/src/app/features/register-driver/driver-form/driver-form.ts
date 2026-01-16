import { Component, computed, effect, EventEmitter, Output, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-driver-form',
  imports: [FormsModule],
  templateUrl: './driver-form.html',
  styleUrl: './driver-form.css',
})
export class DriverForm {
  @Output() dataChange = new EventEmitter<any>();

  private emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  email = signal('');
  isEmailValid = computed(() => {
    return this.emailRegex.test(this.email());
  });

  password = signal('');
  name = signal('');
  surname = signal('');
  address = signal('');
  phoneNumber = signal('');

  showPassword = signal(false);

  constructor() {
    effect(() => {
      this.dataChange.emit({
        email: this.email(),
        password: this.password(),
        name: this.name(),
        surname: this.surname(),
        address: this.address(),
        phoneNumber: this.phoneNumber(),
        isEmailValid: this.isEmailValid()
      });
    });
  }

  togglePasswordVisibility() {
    this.showPassword.update((val) => !val);
  }
}
