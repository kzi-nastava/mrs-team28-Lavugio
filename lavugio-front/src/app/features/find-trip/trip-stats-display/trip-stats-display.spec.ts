import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TripStatsDisplay } from './trip-stats-display';

describe('TripStatsDisplay', () => {
  let component: TripStatsDisplay;
  let fixture: ComponentFixture<TripStatsDisplay>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TripStatsDisplay]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TripStatsDisplay);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
