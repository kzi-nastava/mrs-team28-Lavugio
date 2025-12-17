import {ChangeDetectionStrategy, Component} from '@angular/core';
import {provideNativeDateAdapter} from '@angular/material/core';
import {MatDatepickerModule} from '@angular/material/datepicker';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatInputModule} from '@angular/material/input';
import { MAT_DATE_LOCALE } from '@angular/material/core';

@Component({
  selector: 'app-date-range-picker',
  templateUrl: 'date-range-picker.html',
  imports: [MatFormFieldModule, MatDatepickerModule, MatInputModule],
  providers: [provideNativeDateAdapter(),
    {provide: MAT_DATE_LOCALE, useValue: 'en-GB'}],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DateRangePicker {}
