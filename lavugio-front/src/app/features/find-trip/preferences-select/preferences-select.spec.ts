import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PreferencesSelect } from './preferences-select';

describe('PreferencesSelect', () => {
  let component: PreferencesSelect;
  let fixture: ComponentFixture<PreferencesSelect>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PreferencesSelect]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PreferencesSelect);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
