import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FavoriteRoute } from '@app/shared/models/favoriteRoute';

@Component({
  selector: 'app-favorite-routes-dialog',
  imports: [],
  templateUrl: './favorite-routes-dialog.html',
  styleUrl: './favorite-routes-dialog.css',
})
export class FavoriteRoutesDialog {
  @Input() routes: FavoriteRoute[] = [];

  @Output() cancel = new EventEmitter<void>();
  @Output() routeSelected = new EventEmitter<FavoriteRoute>();

  selectedRoute: FavoriteRoute | null = null;

  selectRoute(route: FavoriteRoute) {
    this.selectedRoute = route;
  }

  confirm() {
    if (this.selectedRoute) {
      this.routeSelected.emit(this.selectedRoute);
    }
  }

  close() {
    this.cancel.emit();
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