import { Component, input, output, signal } from '@angular/core';
import { RideHistoryUserDetailedModel } from '@app/shared/models/ride/rideHistoryUserDetailed';
import { CommonModule } from '@angular/common';
import { ReviewForm } from "@app/shared/components/review-form/review-form";

@Component({
  selector: 'app-review-section',
  imports: [CommonModule, ReviewForm],
  templateUrl: './review-section.html',
  styleUrl: './review-section.css',
})
export class ReviewSection {
  info = input.required<RideHistoryUserDetailedModel | null>();
  showReviewForm = signal(false);
  reviewClicked = output<void>();


  getStarArray(rating: number | null): number[] {
    const stars = rating || 0;
    return Array(5)
      .fill(0)
      .map((_, i) => (i < stars ? 1 : 0));
  }

  isReviewable(): boolean {
    const rideEndStr = this.info()?.end as unknown as string; // očekujemo string sada
    if (!rideEndStr) return false;

    // Parsiranje stringa "HH:mm dd.MM.yyyy"
    const dateTimeRegex =   /^(\d{2}):(\d{2}) (\d{2})\.(\d{2})\.(\d{4})$/;
    const match = rideEndStr.match(dateTimeRegex);

    if (!match) return false;

    const [, hh, mm, dd, MM, yyyy] = match;
    const rideEnd = new Date(
      Number(yyyy),
      Number(MM) - 1, // meseci su 0-indexirani
      Number(dd),
      Number(hh),
      Number(mm)
    );

    const now = new Date();
    const diffMs = now.getTime() - rideEnd.getTime();
    const diffDays = diffMs / (1000 * 60 * 60 * 24);

    return diffDays <= 3;
  }


  openReviewForm() {
    this.showReviewForm.set(true); 
  }


  successfulForm(){
    this.info()!.hasReview = true;
    this.showReviewForm.set(false);
  }
}
