import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'app-success-dialog',
  imports: [],
  templateUrl: './success-dialog.html',
  styleUrl: './success-dialog.css',
})
export class SuccessDialog {
  @Input() title: string = 'Error';
  @Input() message: string = 'Something went wrong.';
  @Output() closed = new EventEmitter<void>();

  close() {
    this.closed.emit();
  }
}
