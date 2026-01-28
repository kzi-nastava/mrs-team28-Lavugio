import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-prompt-dialog',
  imports: [FormsModule],
  standalone: true,
  templateUrl: './prompt-dialog.html',
  styleUrl: './prompt-dialog.css',
})
export class PromptDialog {
  @Input() title: string = 'Input Required';
  @Input() message: string = 'Please enter a value:';
  @Input() placeholder: string = '';
  @Input() required: boolean = true;
  @Output() result = new EventEmitter<string | null>();

  inputValue: string = '';

  confirm() {
    if (this.required && !this.inputValue.trim()) {
      return;
    }
    this.result.emit(this.inputValue.trim());
  }

  cancel() {
    this.result.emit(null);
  }
}
