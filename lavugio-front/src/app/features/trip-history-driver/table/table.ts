import { Component } from '@angular/core';
import {Row} from './row/row';
import {Header} from './header/header';

@Component({
  selector: 'app-table',
  imports: [Row, Header],
  templateUrl: './table.html',
  styleUrl: './table.css',
})
export class Table {

}
