import { Component } from '@angular/core';
import {input} from '@angular/core';
import { Router } from '@angular/router';
@Component({
  selector: 'app-row',
  imports: [],
  templateUrl: './row.html',
  styleUrl: './row.css',
})
export class Row {
  route = input<string>('/');
  startDate = input<string>('');
  endDate = input<string>('');
  startTime = input<string>('');
  endTime = input<string>('');
  departure = input<string>('');
  destination = input<string>('');

  constructor(private router: Router) {}

  navigate() {
    if (this.route) {
      this.router.navigate([this.route()]);
    }
  }
}
