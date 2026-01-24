import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'app-confirm-dialog',
  imports: [],
  standalone: true,
  templateUrl: './confirm-dialog.html',
  styleUrl: './confirm-dialog.css',
})
export class ConfirmDialog {
  @Input() title: string = 'Confirm';
  @Input() message: string = 'Are you sure?';
  @Output() result = new EventEmitter<boolean>();

  confirm() {
    this.result.emit(true);
  }

  cancel() {
    this.result.emit(false);
  }
}
