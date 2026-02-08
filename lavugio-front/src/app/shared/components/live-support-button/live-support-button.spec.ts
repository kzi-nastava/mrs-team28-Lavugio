import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LiveSupportButton } from './live-support-button';

describe('LiveSupportButton', () => {
  let component: LiveSupportButton;
  let fixture: ComponentFixture<LiveSupportButton>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LiveSupportButton]
    })
    .compileComponents();

    fixture = TestBed.createComponent(LiveSupportButton);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
