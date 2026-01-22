import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DriverUpdateRequests } from './driver-update-requests';

describe('DriverUpdateRequests', () => {
  let component: DriverUpdateRequests;
  let fixture: ComponentFixture<DriverUpdateRequests>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DriverUpdateRequests]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DriverUpdateRequests);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
