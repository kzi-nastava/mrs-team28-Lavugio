import { Injectable, signal } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class ProfileEdit {
  isEditMode = signal(false);

  toggleEditMode() {
    this.isEditMode.update(value => !value);
  }

  enableEditMode() {
    this.isEditMode.set(true);
  }

  disableEditMode() {
    this.isEditMode.set(false);
  }
}
