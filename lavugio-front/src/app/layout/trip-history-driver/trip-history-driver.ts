import { Component } from '@angular/core';
import {BaseInfoPage} from '../base-info-page/base-info-page';
import {DateFilter} from '@app/layout/trip-history-driver/date-filter/date-filter';
import {Table} from './table/table';
@Component({
  selector: 'app-trip-history-driver',
  imports: [BaseInfoPage, DateFilter, Table],
  templateUrl: './trip-history-driver.html',
  styleUrl: './trip-history-driver.css',
})
export class TripHistoryDriver {

}
