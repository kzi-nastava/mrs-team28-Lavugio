import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SelectedPreferencesDisplay } from './selected-preferences-display';

describe('SelectedPreferencesDisplay', () => {
  let component: SelectedPreferencesDisplay;
  let fixture: ComponentFixture<SelectedPreferencesDisplay>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SelectedPreferencesDisplay]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SelectedPreferencesDisplay);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
