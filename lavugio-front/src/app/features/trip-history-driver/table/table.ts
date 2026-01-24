import { Component, input, output, ElementRef, viewChild, effect, OnDestroy } from '@angular/core';
import { Row } from './row/row';
import { Header } from './header/header';
import { RideHistoryDriverModel } from '@app/shared/models/ride/rideHistoryDriver';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-table',
  imports: [Row, Header, CommonModule],
  templateUrl: './table.html',
  styleUrl: './table.css',
})
export class Table implements OnDestroy {
  rides = input<RideHistoryDriverModel[]>([]);
  loading = input<boolean>(false);
  loadingDirection = input<'none' | 'older' | 'newer'>('none');
  hasMoreNewer = input<boolean>(false);
  hasMoreOlder = input<boolean>(true);
  
  loadOlder = output<void>();
  loadNewer = output<void>();
  
  topSentinel = viewChild<ElementRef>('topSentinel');
  bottomSentinel = viewChild<ElementRef>('bottomSentinel');
  scrollContainer = viewChild<ElementRef>('scrollContainer');
  
  private topObserver?: IntersectionObserver;
  private bottomObserver?: IntersectionObserver;
  private oldScrollHeight = 0;

  constructor() {
    effect(() => {
      const topEl = this.topSentinel();
      if (topEl) {
        this.setupTopObserver(topEl.nativeElement);
      }
    });

    effect(() => {
      const bottomEl = this.bottomSentinel();
      if (bottomEl) {
        this.setupBottomObserver(bottomEl.nativeElement);
      }
    });

    // Prati promene u listi rides
    effect(() => {
      const rides = this.rides();
      const direction = this.loadingDirection();
      const container = this.scrollContainer()?.nativeElement;
      
      if (!container) return;

      if (direction === 'newer' && rides.length > 0) {
        if (this.oldScrollHeight === 0) {
          this.oldScrollHeight = container.scrollHeight;
        }
      }
      
      if (direction === 'none' && this.oldScrollHeight > 0) {
        const newScrollHeight = container.scrollHeight;
        const heightDiff = newScrollHeight - this.oldScrollHeight;
        
        container.scrollTop = container.scrollTop + heightDiff;
        
        this.oldScrollHeight = 0;
      }
    });
  }

  private setupTopObserver(element: HTMLElement) {
    if (this.topObserver) {
      this.topObserver.disconnect();
    }

    this.topObserver = new IntersectionObserver(
      (entries) => {
        entries.forEach(entry => {
          if (entry.isIntersecting && 
              !this.loading() && 
              this.hasMoreNewer() &&
              this.loadingDirection() === 'none') {
            this.loadNewer.emit();
          }
        });
      },
      {
        root: null,
        rootMargin: '200px',
        threshold: 0.1
      }
    );

    this.topObserver.observe(element);
  }

  private setupBottomObserver(element: HTMLElement) {
    if (this.bottomObserver) {
      this.bottomObserver.disconnect();
    }

    this.bottomObserver = new IntersectionObserver(
      (entries) => {
        entries.forEach(entry => {
          if (entry.isIntersecting && 
              !this.loading() && 
              this.hasMoreOlder() &&
              this.loadingDirection() === 'none') {
            this.loadOlder.emit();
          }
        });
      },
      {
        root: null,
        rootMargin: '200px',
        threshold: 0.1
      }
    );

    this.bottomObserver.observe(element);
  }

  parseDateTime(dateTimeStr: string): { date: string; time: string } {
    if (!dateTimeStr) return { date: '', time: '' };
    
    const parts = dateTimeStr.trim().split(' ');
    if (parts.length === 2) {
      return {
        time: parts[0],
        date: parts[1] 
      };
    }
    
    return { date: '', time: '' };
  }

  ngOnDestroy() {
    if (this.topObserver) {
      this.topObserver.disconnect();
    }
    if (this.bottomObserver) {
      this.bottomObserver.disconnect();
    }
  }
}