import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DriverScheduledRides } from './driver-scheduled-rides';

describe('DriverScheduledRides', () => {
  let component: DriverScheduledRides;
  let fixture: ComponentFixture<DriverScheduledRides>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DriverScheduledRides]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DriverScheduledRides);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
