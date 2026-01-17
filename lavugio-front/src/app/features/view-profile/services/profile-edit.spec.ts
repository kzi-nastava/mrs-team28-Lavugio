import { TestBed } from '@angular/core/testing';

import { ProfileEdit } from './profile-edit';

describe('ProfileEdit', () => {
  let service: ProfileEdit;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ProfileEdit);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
