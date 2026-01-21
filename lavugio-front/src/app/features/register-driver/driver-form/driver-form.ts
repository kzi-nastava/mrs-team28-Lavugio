import { Component, computed, effect, EventEmitter, Input, Output, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-driver-form',
  imports: [FormsModule],
  templateUrl: './driver-form.html',
  styleUrl: './driver-form.css',
})
export class DriverForm {
  @Input() set initialData(data: any) {
    if (data) {
      this.email.set(data.email || '');
      this.name.set(data.name || '');
      this.surname.set(data.surname || '');
      this.address.set(data.address || '');
      this.phoneNumber.set(data.phoneNumber || '');
    }
  }

  @Output() dataChange = new EventEmitter<any>();

  private emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  email = signal('');
  isEmailValid = computed(() => {
    return this.emailRegex.test(this.email());
  });

  name = signal('');
  surname = signal('');
  address = signal('');
  phoneNumber = signal('');

  showPassword = signal(false);
  submitted = signal(false);

  constructor() {
    // Emit data whenever any signal changes
    effect(() => {
      this.email();
      this.name();
      this.surname();
      this.address();
      this.phoneNumber();
      this.emitData();
    });
  }

  emitData() {
    this.dataChange.emit({
      email: this.email(),
      name: this.name(),
      surname: this.surname(),
      address: this.address(),
      phoneNumber: this.phoneNumber(),
    });
  }

  togglePasswordVisibility() {
    this.showPassword.update((val) => !val);
  }

  onSubmit() {
    this.submitted.set(true);
  }

  isFormValid(): boolean {
    return this.isEmailValid() && !!this.name() && !!this.surname() && !!this.address() && !!this.phoneNumber();
  }
}
