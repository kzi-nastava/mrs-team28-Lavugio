import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'app-blocked-dialog',
  imports: [],
  standalone: true,
  templateUrl: './blocked-dialog.html',
  styleUrl: './blocked-dialog.css',
})
export class BlockedDialog {
  @Input() reason: string = 'Your account has been blocked.';
  @Output() closed = new EventEmitter<void>();

  close() {
    this.closed.emit();
  }
}
