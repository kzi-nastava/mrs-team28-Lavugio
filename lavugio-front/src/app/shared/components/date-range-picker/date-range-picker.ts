import {ChangeDetectionStrategy, Component, input, output} from '@angular/core';
import {provideNativeDateAdapter} from '@angular/material/core';
import {DateRange, MatDatepickerModule} from '@angular/material/datepicker';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatInputModule} from '@angular/material/input';
import { MAT_DATE_LOCALE } from '@angular/material/core';

@Component({
  selector: 'app-date-range-picker',
  templateUrl: 'date-range-picker.html',
  styleUrl: 'date-range-picker.css',
  imports: [MatFormFieldModule, MatDatepickerModule, MatInputModule],
  providers: [provideNativeDateAdapter(),
    {provide: MAT_DATE_LOCALE, useValue: 'en-GB'}],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DateRangePicker {

  noPadding = input(false);
  fullWidth = input(false);

  dateRangeSelected = output<{ startDate: string; endDate: string }>();

  emitRange(range: DateRange<Date> | null) {
    if (!range?.start || !range?.end) return;
    console.log(this.formatDate(range.start));
    console.log(this.formatDate(range.end));
    this.dateRangeSelected.emit({
      startDate: this.formatDate(range.start),
      endDate: this.formatDate(range.end),
    });
  }

  private formatDate(date: Date): string {
    const day = String(date.getDate()).padStart(2, '0');
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const year = date.getFullYear();

    return `${day}/${month}/${year}`; 
  }
}
