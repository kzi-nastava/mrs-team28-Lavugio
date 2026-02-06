import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PriceDefinitionComponent } from './price-definition';

describe('PriceDefinition', () => {
  let component: PriceDefinitionComponent;
  let fixture: ComponentFixture<PriceDefinitionComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PriceDefinitionComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PriceDefinitionComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
