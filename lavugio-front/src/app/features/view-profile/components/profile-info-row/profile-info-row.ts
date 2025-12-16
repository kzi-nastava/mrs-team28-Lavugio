import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-profile-info-row',
  imports: [],
  templateUrl: './profile-info-row.html',
  styleUrl: './profile-info-row.css',
})
export class ProfileInfoRow {
  @Input() label = "Label";
  @Input() value = "Value";
}
