import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

export interface PreferenceOption {
  label: string;
  selected: boolean;
}

@Component({
  selector: 'app-selected-preferences-display',
  imports: [CommonModule],
  templateUrl: './selected-preferences-display.html',
  styleUrl: './selected-preferences-display.css',
})

export class SelectedPreferencesDisplay {
  @Input() vehicleType: string = '';
  @Input() isPetFriendly: boolean = false;
  @Input() isBabyFriendly: boolean = false;
  @Input() hasError: boolean = false;
}
