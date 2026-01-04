import { Component } from '@angular/core';
import {DateRangePicker} from '@app/components/date-range-picker/date-range-picker'
@Component({
  selector: 'app-date-filter',
  imports: [DateRangePicker],
  templateUrl: './date-filter.html',
  styleUrl: './date-filter.css',
})
export class DateFilter {

}
