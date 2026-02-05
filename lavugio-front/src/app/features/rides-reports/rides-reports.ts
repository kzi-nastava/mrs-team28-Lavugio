import { Component } from '@angular/core';
import { Navbar } from '@app/shared/components/navbar/navbar';
import { WhiteSheetBackground } from '@app/shared/components/white-sheet-background/white-sheet-background';

@Component({
  selector: 'app-rides-reports',
  imports: [Navbar, WhiteSheetBackground],
  templateUrl: './rides-reports.html',
  styleUrl: './rides-reports.css',
})
export class RidesReports {

}
