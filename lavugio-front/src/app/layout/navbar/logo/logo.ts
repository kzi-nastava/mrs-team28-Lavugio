import {Component, Input} from '@angular/core';
import { RouterModule } from '@angular/router';
@Component({
  selector: 'app-logo',
  imports: [RouterModule],
  templateUrl: './logo.html',
  styleUrl: './logo.css',
})
export class Logo {
  @Input() route = '/';
}
