import { Component, input, InputSignal } from '@angular/core';

@Component({
  selector: 'app-driver-update-request-row',
  imports: [],
  templateUrl: './driver-update-request-row.html',
  styleUrl: './driver-update-request-row.css',
})
export class DriverUpdateRequestRow {
  label: InputSignal<string> = input.required<string>();
  oldValue: InputSignal<string> = input.required<string>();
  newValue: InputSignal<string> = input.required<string>();

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
