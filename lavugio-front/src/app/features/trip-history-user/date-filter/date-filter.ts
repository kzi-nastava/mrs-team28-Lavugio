import { Component, output } from '@angular/core';
import { DateRangePicker } from '@app/shared/components/date-range-picker/date-range-picker';

@Component({
  selector: 'app-date-filter',
  imports: [DateRangePicker],
  templateUrl: './date-filter.html',
  styleUrl: './date-filter.css',
})
export class DateFilter {
  dateRangeSelected = output<any>();

  emitRangeSelected(event: { startDate: string; endDate: string }) {
    this.dateRangeSelected.emit(event);
  }
}
