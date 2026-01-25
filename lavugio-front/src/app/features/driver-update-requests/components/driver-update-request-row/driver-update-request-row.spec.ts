import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DriverUpdateRequestRow } from './driver-update-request-row';

describe('DriverUpdateRequestRow', () => {
  let component: DriverUpdateRequestRow;
  let fixture: ComponentFixture<DriverUpdateRequestRow>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DriverUpdateRequestRow]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DriverUpdateRequestRow);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
