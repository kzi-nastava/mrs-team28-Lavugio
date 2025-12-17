import { CommonModule } from '@angular/common';
import { Component, EventEmitter, input, Input, output, Output } from '@angular/core';

@Component({
  selector: 'app-button',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './button.html',
  styleUrl: './button.css',
})
export class Button {
  variant = input<'light-green' | 'dark-green' | 'light-brown' | 'dark-brown' | 'outline'>('light-brown');
  size = input<'small' | 'medium' | 'large'>('medium');
  disabled = input<boolean>(false);
  fullWidth = input<boolean>(false);
  fill = input<boolean>(true);
  type = input<'button' | 'submit' | 'reset'>('button');
  icon = input<string | undefined>(undefined);
  
  clicked = output<void>();

  onClick() {
    if (!this.disabled()) {
      this.clicked.emit();
    }
  }
}
