if ((window as any).global === undefined) {
  (window as any).global = window;
}

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReviewForm } from './review-form';
import { RideService } from '@app/core/services/ride-service';
import { Observable, of, throwError } from 'rxjs';
import { RideReview } from '@app/shared/models/ride/rideReview';

const mockReview: RideReview = {
  rideId: 1,
  driverRating: 5,
  vehicleRating: 4,
  comment: 'Good ride'
};

describe('ReviewForm', () => {
  let component: ReviewForm;
  let fixture: ComponentFixture<ReviewForm>;
  let rideServiceMock: jasmine.SpyObj<RideService>;

  beforeEach(async () => {
    rideServiceMock = jasmine.createSpyObj('RideService', ['postRideReview']);

    await TestBed.configureTestingModule({
      imports: [ReviewForm],
      providers: [
        { provide: RideService, useValue: rideServiceMock }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ReviewForm);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('rideId', 1);

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should sanitize input correctly', () => {
    component.commentControl.setValue('Test@@@!!!###');
    component.onInput();

    expect(component.commentControl.value).toBe('Test!!!');
  });

  it('should not send review if ratings are 0', () => {
    component.sendReview();
    expect(rideServiceMock.postRideReview).not.toHaveBeenCalled();
  });

  it('should send review successfully', () => {
    rideServiceMock.postRideReview.and.returnValue(of(mockReview));

    component.setDriverRating(5);
    component.setVehicleRating(4);
    component.commentControl.setValue('Good ride');

    spyOn(component.isSuccessfulOutput, 'emit');

    component.sendReview();

    expect(rideServiceMock.postRideReview).toHaveBeenCalled();
    expect(component.isDone()).toBeTrue();
    expect(component.isLoading()).toBeFalse();
    expect(component.isSuccessfulOutput.emit).toHaveBeenCalled();
  });

  it('should handle review failure', () => {
    rideServiceMock.postRideReview.and.returnValue(throwError(() => new Error()));

    component.setDriverRating(5);
    component.setVehicleRating(5);
    component.commentControl.setValue('Test');

    component.sendReview();

    expect(component.isFailed()).toBeTrue();
    expect(component.isLoading()).toBeFalse();
  });

  it('should submit on Ctrl+Enter', () => {
    rideServiceMock.postRideReview.and.returnValue(of(mockReview));

    component.setDriverRating(5);
    component.setVehicleRating(5);

    const event = new KeyboardEvent('keydown', {
      ctrlKey: true
    });

    spyOn(event, 'preventDefault');
    spyOn(component, 'sendReview');

    component.onEnterPress(event);

    expect(event.preventDefault).toHaveBeenCalled();
    expect(component.sendReview).toHaveBeenCalled();
  });

  it('should emit hideReviewOutput on destroy', () => {
    spyOn(component.hideReviewOutput, 'emit');

    component.ngOnDestroy();

    expect(component.hideReviewOutput.emit).toHaveBeenCalled();
    expect(component.isHidden()).toBeTrue();
  });
});
