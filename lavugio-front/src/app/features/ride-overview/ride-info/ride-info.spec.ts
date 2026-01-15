import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RideInfo } from './ride-info';

describe('RideInfo', () => {
  let component: RideInfo;
  let fixture: ComponentFixture<RideInfo>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RideInfo]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RideInfo);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
