import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TripHistoryDriver } from './trip-history-driver';

describe('TripHistoryDriver', () => {
  let component: TripHistoryDriver;
  let fixture: ComponentFixture<TripHistoryDriver>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TripHistoryDriver]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TripHistoryDriver);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
