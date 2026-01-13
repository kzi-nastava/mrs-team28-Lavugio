import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddPassangerInput } from './add-passanger-input';

describe('AddPassangerInput', () => {
  let component: AddPassangerInput;
  let fixture: ComponentFixture<AddPassangerInput>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AddPassangerInput]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AddPassangerInput);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
