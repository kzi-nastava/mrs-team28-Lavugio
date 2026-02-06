import { TestBed } from '@angular/core/testing';

import { PriceDefinitionService } from './price-definition-service';

describe('PriceDefinitionService', () => {
  let service: PriceDefinitionService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(PriceDefinitionService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
