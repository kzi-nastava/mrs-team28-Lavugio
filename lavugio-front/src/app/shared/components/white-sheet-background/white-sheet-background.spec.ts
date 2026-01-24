import { ComponentFixture, TestBed } from '@angular/core/testing';

import { WhiteSheetBackground } from './white-sheet-background';

describe('WhiteSheetBackground', () => {
  let component: WhiteSheetBackground;
  let fixture: ComponentFixture<WhiteSheetBackground>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [WhiteSheetBackground]
    })
    .compileComponents();

    fixture = TestBed.createComponent(WhiteSheetBackground);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
