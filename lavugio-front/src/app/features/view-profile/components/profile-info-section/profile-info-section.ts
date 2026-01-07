import { Component } from '@angular/core';
import { ProfileInfoRow } from '../profile-info-row/profile-info-row';
import { Button } from "../../../../shared/components/button/button";

@Component({
  selector: 'app-profile-info-section',
  imports: [ProfileInfoRow, Button],
  templateUrl: './profile-info-section.html',
  styleUrl: './profile-info-section.css',
})
export class ProfileInfoSection {

}
