import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TripInfo } from './trip-info';

describe('TripInfo', () => {
  let component: TripInfo;
  let fixture: ComponentFixture<TripInfo>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TripInfo]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TripInfo);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
