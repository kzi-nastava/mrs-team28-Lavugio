import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FormBackgroundSheet } from './form-background-sheet';

describe('FormBackgroundSheet', () => {
  let component: FormBackgroundSheet;
  let fixture: ComponentFixture<FormBackgroundSheet>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FormBackgroundSheet]
    })
    .compileComponents();

    fixture = TestBed.createComponent(FormBackgroundSheet);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
