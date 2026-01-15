import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'app-error-dialog',
  imports: [],
  standalone: true,
  templateUrl: './error-dialog.html',
  styleUrl: './error-dialog.css',
})
export class ErrorDialog {
  @Input() title: string = 'Error';
  @Input() message: string = 'Something went wrong.';
  @Output() closed = new EventEmitter<void>();

  close() {
    this.closed.emit();
  }
}
