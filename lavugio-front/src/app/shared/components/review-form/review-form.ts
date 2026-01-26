import { Component, inject, OnDestroy, output, signal, WritableSignal } from '@angular/core';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { RideService } from '@app/core/services/ride-service';
import { RideReview } from '@app/shared/models/ride/rideReview';

@Component({
  selector: 'app-review-form',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './review-form.html',
})
export class ReviewForm implements OnDestroy {

  private allowedRegex = /[^a-zA-Z0-9 .,!?-]/g;
  private rideService = inject(RideService);

  isDone: WritableSignal<Boolean> = signal(false);
  isHidden: WritableSignal<Boolean> = signal(false);
  isFailed: WritableSignal<Boolean> = signal(false);
  isLoading: WritableSignal<Boolean> = signal(false);
  
  driverRating: WritableSignal<number> = signal(0);
  vehicleRating: WritableSignal<number> = signal(0);
  
  rideId: number = 0;

  hideReviewOutput = output();
  isSuccessfulOutput = output();

  commentControl = new FormControl('', {
    nonNullable: true
  });

  readonly maxLength = 256;

  setDriverRating(rating: number): void {
    this.driverRating.set(rating);
  }

  setVehicleRating(rating: number): void {
    this.vehicleRating.set(rating);
  }

  onInput() {
    const value = this.commentControl.value;

    this.commentControl.setValue(
      value.replace(this.allowedRegex, '').slice(0, this.maxLength),
      { emitEvent: false }
    );
  }

  sendReview() {
    if (this.driverRating() === 0 || this.vehicleRating() === 0) {
      return;
    }

    this.isLoading.set(true);
    this.isFailed.set(false);

    let review: RideReview = {
      rideId: this.rideId,
      driverRating: this.driverRating(),
      vehicleRating: this.vehicleRating(),
      comment: this.commentControl.value
    };

    this.rideService.postRideReview(this.rideId, review).subscribe({
      next: () => {
        console.log("Review successful");
        this.isSuccessfulOutput.emit();
        this.isDone.set(true);
        this.isLoading.set(false);
      },
      error: () => {
        console.error("Review failed");
        this.isFailed.set(true);
        this.isLoading.set(false);
      }
    });
  }

  ngOnDestroy() {
    this.hideReviewOutput.emit();
    this.isHidden.set(true);
  }

  onEnterPress(event: Event) {
    if (this.isLoading()) return;

    if (!(event instanceof KeyboardEvent)) return;

    const value = this.commentControl.value?.trim();

    if ((event.ctrlKey || event.metaKey) && this.driverRating() > 0 && this.vehicleRating() > 0) {
      event.preventDefault();
      this.sendReview();
    }
  }
}