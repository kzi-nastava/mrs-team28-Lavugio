import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BaseInfoPage } from './base-info-page';

describe('BaseInfoPage', () => {
  let component: BaseInfoPage;
  let fixture: ComponentFixture<BaseInfoPage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BaseInfoPage]
    })
    .compileComponents();

    fixture = TestBed.createComponent(BaseInfoPage);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
