import { CommonModule } from '@angular/common';
import { Component, Input, SimpleChanges } from '@angular/core';

export interface TripStat {
  label: string;
  value: string;
}

@Component({
  selector: 'app-trip-stats-display',
  imports: [CommonModule],
  templateUrl: './trip-stats-display.html',
  styleUrl: './trip-stats-display.css',
})
export class TripStatsDisplay {
  @Input() distance: string = '0km';
  @Input() estimatedTime: string = '0min';
  @Input() price: string = '0$';

  ngOnChanges(changes: SimpleChanges) {
    // This runs whenever any @Input changes
    // Add call to the endpoint
    console.log('Trip stats updated:', {
      distance: this.distance,
      time: this.estimatedTime,
      price: this.price
    });
  }
}
