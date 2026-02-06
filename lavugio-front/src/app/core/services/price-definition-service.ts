import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { RidePriceModel } from '@app/shared/models/ridePrice';
import { environment } from '@environments/environment';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class PriceDefinitionService {
  url = environment.BACKEND_URL + "/api/rides"
  http = inject(HttpClient);

  getPrices(): Observable<RidePriceModel> {
    return this.http.get<RidePriceModel>(this.url + "/prices");
  }

  postPrices(pricesModel: RidePriceModel): Observable<RidePriceModel> {
    return this.http.post<RidePriceModel>(this.url + "/prices", pricesModel);
  }
}
