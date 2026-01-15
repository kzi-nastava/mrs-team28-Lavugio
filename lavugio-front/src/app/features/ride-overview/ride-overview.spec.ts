import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RideOverview } from './ride-overview';

describe('RideOverview', () => {
  let component: RideOverview;
  let fixture: ComponentFixture<RideOverview>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RideOverview]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RideOverview);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
