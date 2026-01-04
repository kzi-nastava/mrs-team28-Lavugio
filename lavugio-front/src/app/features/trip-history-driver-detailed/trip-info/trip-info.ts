import { Component } from '@angular/core';

@Component({
  selector: 'app-trip-info',
  imports: [],
  templateUrl: './trip-info.html',
  styleUrl: './trip-info.css',
})
export class TripInfo {
  begin: string = "20.12.2024 14:30";
  end: string = "20.12.2024 16:45";
  departure: string = "Novi Sad, Bulevar oslobođenja 46";
  destination: string = "Beograd, Trg Republike 5";
  price: string = "850.00 ";
  cancelled: string = "false";
  panic: boolean = false;
}
