import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProfileInfoSection } from './profile-info-section';

describe('ProfileInfoSection', () => {
  let component: ProfileInfoSection;
  let fixture: ComponentFixture<ProfileInfoSection>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ProfileInfoSection]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ProfileInfoSection);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
