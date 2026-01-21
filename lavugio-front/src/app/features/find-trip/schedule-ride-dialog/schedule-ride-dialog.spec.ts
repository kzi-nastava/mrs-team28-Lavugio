import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ScheduleRideDialog } from './schedule-ride-dialog';

describe('ScheduleRideDialog', () => {
  let component: ScheduleRideDialog;
  let fixture: ComponentFixture<ScheduleRideDialog>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ScheduleRideDialog]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ScheduleRideDialog);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
