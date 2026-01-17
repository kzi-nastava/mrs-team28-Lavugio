import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DestinationsDisplay } from './destinations-display';

describe('DestinationsDisplay', () => {
  let component: DestinationsDisplay;
  let fixture: ComponentFixture<DestinationsDisplay>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DestinationsDisplay]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DestinationsDisplay);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
