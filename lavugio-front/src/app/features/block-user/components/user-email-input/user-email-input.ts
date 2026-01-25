import {
  Component,
  EventEmitter,
  Input,
  Output,
  OnDestroy,
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  output,
  inject,
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { Subject } from 'rxjs';
import { debounceTime, distinctUntilChanged, switchMap, takeUntil, tap } from 'rxjs/operators';
import { UserService } from '@app/core/services/user/user-service';

export interface UserEmailResult {
  email: string;
}

@Component({
  selector: 'app-user-email-input',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './user-email-input.html',
  styleUrl: './user-email-input.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class UserEmailInput implements OnDestroy {
  @Input() placeholder: string = 'Enter user email...';
  @Input() value: string = '';
  @Output() valueChange = new EventEmitter<string>();
  @Output() emailSelected = new EventEmitter<UserEmailResult>();

  emailPickRequested = output<void>();

  activateMapPickMode() {
    this.emailPickRequested.emit();
    console.log('Email pick mode activated');
  }

  suggestions: UserEmailResult[] = [];
  showSuggestions = false;
  isLoading = false;

  private searchSubject = new Subject<string>();
  private destroy$ = new Subject<void>();
  private userService = inject(UserService);
  private cdr = inject(ChangeDetectorRef);

  constructor() {
    this.setupSearch();
  }

  private setupSearch() {
    this.searchSubject
      .pipe(
        debounceTime(300),
        distinctUntilChanged(),
        tap(() => {
          this.isLoading = true;
          this.cdr.markForCheck();
        }),
        switchMap((query) =>
          this.userService.searchUserEmails(query)
        ),
        takeUntil(this.destroy$),
      )
      .subscribe({
        next: (results: UserEmailResult[]) => {
          this.suggestions = results;
          this.showSuggestions = results.length > 0;
          this.isLoading = false;
          this.cdr.markForCheck();
        },
        error: (err: any) => {
          console.error('Error fetching email suggestions:', err);
          this.suggestions = [];
          this.showSuggestions = false;
          this.isLoading = false;
          this.cdr.markForCheck();
        },
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

  selectSuggestion(suggestion: UserEmailResult) {
    this.value = suggestion.email;
    this.valueChange.emit(suggestion.email);
    this.emailSelected.emit(suggestion);
    this.showSuggestions = false;
    this.suggestions = [];
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
