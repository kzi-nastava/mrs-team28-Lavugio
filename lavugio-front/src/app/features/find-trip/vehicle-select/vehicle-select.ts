import { Component, Input, Output, EventEmitter, OnInit } from '@angular/core';

@Component({
  selector: 'app-vehicle-select',
  imports: [],
  templateUrl: './vehicle-select.html',
  styleUrl: './vehicle-select.css',
})
export class VehicleSelect implements OnInit {
  @Input() options: string[] = [];
  @Input() placeholder = 'Select vehicle type';
  @Input() initialSelected: string = '';
  @Output() vehicleSelected = new EventEmitter<string>();

  isOpen = false;
  selected: string | null = null;

  ngOnInit() {
    if (this.initialSelected) {
      this.selected = this.initialSelected;
    }
  }

  toggle() {
    this.isOpen = !this.isOpen;
  }

  select(option: string) {
    this.selected = option;
    this.isOpen = false;
    this.vehicleSelected.emit(option);
  }
}
