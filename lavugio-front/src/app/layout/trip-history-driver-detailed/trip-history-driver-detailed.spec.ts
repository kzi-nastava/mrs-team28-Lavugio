import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TripHistoryDriverDetailed } from './trip-history-driver-detailed';

describe('TripHistoryDriverDetailed', () => {
  let component: TripHistoryDriverDetailed;
  let fixture: ComponentFixture<TripHistoryDriverDetailed>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TripHistoryDriverDetailed]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TripHistoryDriverDetailed);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
