import { Component } from '@angular/core';
import {input} from '@angular/core';
@Component({
  selector: 'app-row',
  imports: [],
  templateUrl: './row.html',
  styleUrl: './row.css',
})
export class Row {
  startDate = input<string>('');
  endDate = input<string>('');
  startTime = input<string>('');
  endTime = input<string>('');
  departure = input<string>('');
  destination = input<string>('');
}
