import { ComponentFixture, TestBed } from '@angular/core/testing';

import {RideHistoryDriverDetailed} from './trip-history-driver-detailed'

describe('TripHistoryDriverDetailed', () => {
  let component: RideHistoryDriverDetailed;
  let fixture: ComponentFixture<RideHistoryDriverDetailed>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RideHistoryDriverDetailed]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RideHistoryDriverDetailed);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
