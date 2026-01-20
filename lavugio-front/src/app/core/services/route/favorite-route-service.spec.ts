import { TestBed } from '@angular/core/testing';

import { FavoriteRouteService } from './favorite-route-service';

describe('FavoriteRouteService', () => {
  let service: FavoriteRouteService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(FavoriteRouteService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
