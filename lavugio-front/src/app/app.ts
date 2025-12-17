import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import {BaseInfoPage} from './layout/base-info-page/base-info-page';
@Component({
  selector: 'app-root',
  imports: [RouterOutlet, BaseInfoPage],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  protected readonly title = signal('lavugio-front');
}
