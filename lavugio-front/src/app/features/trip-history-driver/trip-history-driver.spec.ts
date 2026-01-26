import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RideHistoryDriver } from './trip-history-driver';

describe('TripHistoryDriver', () => {
  let component: RideHistoryDriver;
  let fixture: ComponentFixture<RideHistoryDriver>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RideHistoryDriver]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RideHistoryDriver);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
