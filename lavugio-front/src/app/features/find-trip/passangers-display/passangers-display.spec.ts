import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PassangersDisplay } from './passangers-display';

describe('PassangersDisplay', () => {
  let component: PassangersDisplay;
  let fixture: ComponentFixture<PassangersDisplay>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PassangersDisplay]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PassangersDisplay);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
