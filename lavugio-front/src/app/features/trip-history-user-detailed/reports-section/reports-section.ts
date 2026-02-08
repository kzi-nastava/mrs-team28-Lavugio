import { Component, input } from '@angular/core';
import { RideHistoryUserDetailedModel } from '@app/shared/models/ride/rideHistoryUserDetailed';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-reports-section',
  imports: [CommonModule],
  templateUrl: './reports-section.html',
  styleUrl: './reports-section.css',
})
export class ReportsSection {
  info = input<RideHistoryUserDetailedModel | null>();
}
