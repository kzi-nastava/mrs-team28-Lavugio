import { Component, input } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-row',
  imports: [],
  templateUrl: './row.html',
  styleUrl: './row.css',
})
export class Row {
  rideId = input<number>(0);
  startDate = input<string>('');
  endDate = input<string>('');
  startTime = input<string>('');
  endTime = input<string>('');
  departure = input<string>('');
  destination = input<string>('');

  constructor(private router: Router) {}

  navigate() {
    const id = this.rideId();
    if (id != null) {
      this.router.navigate([`ride-history-user/${id}`]);
    } else {
      console.log(id);
    }
  }
}
