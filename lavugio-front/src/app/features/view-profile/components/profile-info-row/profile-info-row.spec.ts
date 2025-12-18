import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProfileInfoRow } from './profile-info-row';

describe('ProfileInfoRow', () => {
  let component: ProfileInfoRow;
  let fixture: ComponentFixture<ProfileInfoRow>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ProfileInfoRow]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ProfileInfoRow);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
