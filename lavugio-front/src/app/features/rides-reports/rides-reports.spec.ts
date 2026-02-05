import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RidesReports } from './rides-reports';

describe('RidesReports', () => {
  let component: RidesReports;
  let fixture: ComponentFixture<RidesReports>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RidesReports]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RidesReports);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
