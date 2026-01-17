// destination-selector.component.ts
import {
  Component,
  EventEmitter,
  Input,
  Output,
  OnDestroy,
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  output,
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { Subject } from 'rxjs';
import { debounceTime, distinctUntilChanged, switchMap, takeUntil, tap } from 'rxjs/operators';
import { GeocodeResult, GeocodingService } from '../geocoding-service/geocoding-service';
import { Coordinates } from '@app/shared/models/coordinates';

@Component({
  selector: 'app-destination-selector',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './destination-selector.html',
  styleUrls: ['./destination-selector.css'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DestinationSelector implements OnDestroy {
  @Input() placeholder: string = 'Enter a destination...';
  @Input() value: string = '';
  @Output() valueChange = new EventEmitter<string>();
  @Output() destinationSelected = new EventEmitter<GeocodeResult>();

  mapPickRequested = output<void>();

  activateMapPickMode() {
    this.mapPickRequested.emit();
    console.log('Map pick mode activated');
  }

  setLocationFromMap(coords: Coordinates) {
    this.isLoading = true;

    this.geocodingService
      .reverseGeocode(coords.latitude, coords.longitude)
      .pipe(takeUntil(this.destroy$))
      .subscribe((result) => {
        this.isLoading = false;

        if (result) {
          this.selectSuggestion(result);
        } else {
          const fallback: GeocodeResult = {
            display_name: `Location (${coords.latitude}, ${coords.longitude})`,
            lat: coords.latitude.toString(),
            lon: coords.longitude.toString(),
            type: 'point',
          };

          this.selectSuggestion(fallback);
        }
        this.cdr.markForCheck();
      });
  }

  suggestions: GeocodeResult[] = [];
  showSuggestions = false;
  isLoading = false;

  private searchSubject = new Subject<string>();
  private destroy$ = new Subject<void>();

  constructor(private geocodingService: GeocodingService, private cdr: ChangeDetectorRef) {
    this.setupSearch();
  }

  private setupSearch() {
    this.searchSubject
      .pipe(
        debounceTime(300),
        distinctUntilChanged(),
        tap(() => {
          this.isLoading = true;
        }),
        switchMap((query) => this.geocodingService.searchLocations(query)),
        takeUntil(this.destroy$)
      )
      .subscribe((results) => {
        this.suggestions = results;
        this.showSuggestions = results.length > 0;
        this.isLoading = false;

        this.cdr.markForCheck();
      });
  }

  onInputChange(event: Event) {
    const value = (event.target as HTMLInputElement).value;
    this.value = value;
    this.valueChange.emit(value);

    if (value.trim().length === 0) {
      this.showSuggestions = false;
      this.suggestions = [];
    } else {
      this.searchSubject.next(value);
    }
  }

  selectSuggestion(suggestion: GeocodeResult) {
    this.value = suggestion.display_name;
    this.valueChange.emit(suggestion.display_name);
    this.destinationSelected.emit(suggestion);
    this.showSuggestions = false;
    this.suggestions = [];
    this.value = '';
  }

  onAddDestination() {
    if (this.value.trim() && this.suggestions.length > 0) {
      this.selectSuggestion(this.suggestions[0]);
    }
  }

  onBlur() {
    // Delay to allow click on suggestion
    setTimeout(() => {
      this.showSuggestions = false;
    }, 200);
  }

  onFocus() {
    if (this.suggestions.length > 0) {
      this.showSuggestions = true;
    }
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
