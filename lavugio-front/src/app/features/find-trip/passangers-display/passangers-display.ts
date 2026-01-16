import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Passenger } from "../add-passanger-input/add-passanger-input";
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-passangers-display',
  imports: [CommonModule],
  templateUrl: './passangers-display.html',
  styleUrl: './passangers-display.css',
})
export class PassangersDisplay {
  @Input() passangers: Passenger[] = [];
  @Output() passangerRemoved = new EventEmitter<string>();

  @Input() removable = true;
  @Input() maxHeight: string = '14rem';
  removePassanger(id: string) {
    if (!this.removable) {
      return;
    }
    this.passangerRemoved.emit(id);
  }

  addScrollIfNeeded(element: any) {
    const el = element as HTMLElement;
    // Small delay to ensure measurements are accurate after render
    setTimeout(() => {
      const parent = el.parentElement as HTMLElement;
      // Check if text overflows its container
      if (el.scrollWidth > parent.clientWidth) {
        // Calculate animation duration: faster for short text, slower for long text
        const overflow = el.scrollWidth - parent.clientWidth;
        const duration = Math.max(4, Math.min(12, 3 + overflow / 100)); // 4-12 seconds
        el.style.setProperty('--scroll-duration', `${duration}s`);
        el.classList.add('should-scroll');
      }
    }, 10);
  }

  removeScroll(element: any) {
    const el = element as HTMLElement;
    el.classList.remove('should-scroll');
  }
}
