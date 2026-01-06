import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GuestHomePage } from './guest-home-page';

describe('GuestHomePage', () => {
  let component: GuestHomePage;
  let fixture: ComponentFixture<GuestHomePage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GuestHomePage]
    })
    .compileComponents();

    fixture = TestBed.createComponent(GuestHomePage);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
