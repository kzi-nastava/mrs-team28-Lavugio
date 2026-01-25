import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserEmailInput } from './user-email-input';

describe('UserEmailInput', () => {
  let component: UserEmailInput;
  let fixture: ComponentFixture<UserEmailInput>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UserEmailInput]
    })
    .compileComponents();

    fixture = TestBed.createComponent(UserEmailInput);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
