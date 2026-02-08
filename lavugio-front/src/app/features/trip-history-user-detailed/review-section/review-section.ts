import { Component, input } from '@angular/core';
import { RideHistoryUserDetailedModel } from '@app/shared/models/ride/rideHistoryUserDetailed';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-review-section',
  imports: [CommonModule],
  templateUrl: './review-section.html',
  styleUrl: './review-section.css',
})
export class ReviewSection {
  info = input<RideHistoryUserDetailedModel | null>();

  getStarArray(rating: number | null): number[] {
    const stars = rating || 0;
    return Array(5)
      .fill(0)
      .map((_, i) => (i < stars ? 1 : 0));
  }
}
