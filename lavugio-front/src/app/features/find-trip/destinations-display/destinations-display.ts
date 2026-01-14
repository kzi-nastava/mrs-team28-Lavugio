import { CommonModule } from '@angular/common';
import { Component, ElementRef, EventEmitter, Input, Output, ViewChild } from '@angular/core';
import { Destination } from '@app/shared/models/destination';
import { TripDestination } from '@app/shared/models/tripDestination';

@Component({
  selector: 'app-destinations-display',
  imports: [CommonModule],
  templateUrl: './destinations-display.html',
  styleUrl: './destinations-display.css',
})
export class DestinationsDisplay {
  @Input() destinations: TripDestination[] = [];
  @Output() destinationRemoved = new EventEmitter<string>();

  @Input() removable = true;
  @Input() maxHeight: string = '18rem';

  @Input() hasError: boolean = false;
  
  removeDestination(id: string) {
    if (!this.removable) {
      return;
    }
    this.destinationRemoved.emit(id);
  }

  addScrollIfNeeded(element: any) {
    const el = element as HTMLElement;
    setTimeout(() => {
      const parent = el.parentElement as HTMLElement;
      // Check if text overflows its container
      if (el.scrollWidth > parent.clientWidth) {
        // Calculate animation duration: faster for short text, slower for long text
        const overflow = el.scrollWidth - parent.clientWidth;
        const duration = Math.max(6, Math.min(16, 4 + overflow / 100)); // 6-16 seconds
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
