import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DriverUpdateRequest } from './driver-update-request';

describe('DriverUpdateRequest', () => {
  let component: DriverUpdateRequest;
  let fixture: ComponentFixture<DriverUpdateRequest>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DriverUpdateRequest]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DriverUpdateRequest);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
