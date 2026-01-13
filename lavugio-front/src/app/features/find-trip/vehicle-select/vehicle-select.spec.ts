import { ComponentFixture, TestBed } from '@angular/core/testing';

import { VehicleSelect } from './vehicle-select';

describe('VehicleSelect', () => {
  let component: VehicleSelect;
  let fixture: ComponentFixture<VehicleSelect>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [VehicleSelect]
    })
    .compileComponents();

    fixture = TestBed.createComponent(VehicleSelect);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
