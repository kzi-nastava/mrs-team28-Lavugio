import { Component, output } from '@angular/core';

@Component({
  selector: 'app-header',
  imports: [],
  templateUrl: './header.html',
  styleUrl: './header.css',
})
export class Header {
  sorting : 'ASC' | 'DESC' = 'DESC'
  sortBy: 'START' | 'DEPARTURE' | 'DESTINATION' = 'START'
  updateTableOutput = output<any>();

  updateSortBy(output: 'START' | 'DEPARTURE' | 'DESTINATION'){
    console.log("filters applied")
    if (this.sortBy == output){
      if (this.sorting == 'ASC'){
        this.sorting = 'DESC';
      } else{
        this.sorting = 'ASC';
      }
    } else {
      this.sortBy = output;
      this.sorting = 'DESC';
    }
    this.updateTableOutput.emit(output);
  }
}
