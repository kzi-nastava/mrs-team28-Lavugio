import { Component, EventEmitter, Output } from '@angular/core';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-change-password-dialog',
  imports: [FormsModule],
  templateUrl: './change-password-dialog.html',
  styleUrl: './change-password-dialog.css',
})
export class ChangePasswordDialog {
  @Output() closed = new EventEmitter<void>();
  @Output() passwordChanged = new EventEmitter<{ oldPassword: string; newPassword: string }>();

  oldPassword: string = '';
  newPassword: string = '';
  confirmPassword: string = '';

  close() {
    this.closed.emit();
  }

  onChangePassword() {
    if (this.newPassword !== this.confirmPassword) {
      alert('New passwords do not match!');
      return;
    }

    if (!this.oldPassword || !this.newPassword) {
      alert('Please fill in all fields!');
      return;
    }

    if (this.newPassword.length < 8) {
      alert('New password must be at least 8 characters long!');
      return;
    }

    this.passwordChanged.emit({
      oldPassword: this.oldPassword,
      newPassword: this.newPassword
    });
  }
}
