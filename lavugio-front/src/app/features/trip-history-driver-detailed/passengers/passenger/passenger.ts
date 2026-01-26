import { Component, input } from '@angular/core';
import { PassengerModel } from '@app/shared/models/user/passenger';
import { environment } from '@environments/environment';

@Component({
  selector: 'app-passenger',
  imports: [],
  templateUrl: './passenger.html',
  styleUrl: './passenger.css',
})
export class Passenger {
  backendUrl = environment.BACKEND_URL;
  passenger = input<PassengerModel>();
}
