import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FavoriteRoutesDialog } from './favorite-routes-dialog';

describe('FavoriteRoutesDialog', () => {
  let component: FavoriteRoutesDialog;
  let fixture: ComponentFixture<FavoriteRoutesDialog>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FavoriteRoutesDialog]
    })
    .compileComponents();

    fixture = TestBed.createComponent(FavoriteRoutesDialog);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
