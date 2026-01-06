import { Component, Input } from '@angular/core';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-link',
  imports: [RouterModule],
  templateUrl: './link.html',
  styleUrl: './link.css',
})
export class Link {
  @Input() route = '/';
}
