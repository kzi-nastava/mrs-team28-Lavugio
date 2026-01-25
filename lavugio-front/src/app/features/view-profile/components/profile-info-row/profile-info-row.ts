import { Component, effect, inject, input, Input, output, signal } from '@angular/core';
import { ProfileEdit } from '../../services/profile-edit';

@Component({
  selector: 'app-profile-info-row',
  imports: [],
  templateUrl: './profile-info-row.html',
  styleUrl: './profile-info-row.css',
})
export class ProfileInfoRow {
  label = input.required<string>();
  value = input.required<string | boolean>();
  width = input<'half' | 'full'>('half');
  type = input<'text' | 'email' | 'tel'>('text');
  editable = input<boolean>(true);
  singleLineLabel = input<boolean>(false);
  isBoolean = input<boolean>(false);
  options = input<string[]>();

  valueChanged = output<string>();
  editableValue = signal<string>('');
  editService = inject(ProfileEdit);

  constructor() {
    effect(() => {
      const val = this.value();

      if (typeof val === 'boolean') {
        this.editableValue.set(val ? 'true' : 'false');
      } else {
        this.editableValue.set(val);
      }
    });
  }

  onInputChange(newValue: string) {
    this.editableValue.set(newValue);
    this.valueChanged.emit(newValue);
  }

  onBooleanChange(checked: boolean) {
    const newValue = checked ? 'true' : 'false';
    this.editableValue.set(newValue);
    this.valueChanged.emit(newValue);
  }

  get displayValue(): string {
    const val = this.value();
    
    if (this.isBoolean() || typeof val === 'boolean') {
      if (typeof val === 'boolean') {
        return val ? 'Yes' : 'No';
      } else if (typeof val === 'string') {
        return val === 'true' ? 'Yes' : 'No';
      }
    }
    
    return typeof val === 'string' ? val : String(val);
  }

  get isLongText(): boolean {
    const val = this.value();
    return typeof val === 'string' && val.length > 20;
  }
}
