import { Component, EventEmitter, Input, Output } from '@angular/core';

export interface Passenger {
  id: string;
  email: string;
  name?: string;
}

@Component({
  selector: 'app-add-passanger-input',
  imports: [],
  templateUrl: './add-passanger-input.html',
  styleUrl: './add-passanger-input.css',
})
export class AddPassangerInput {
  @Input() placeholder: string = 'Enter passenger email...';
  @Input() value: string = '';
  @Output() valueChange = new EventEmitter<string>();
  @Output() passengerAdded = new EventEmitter<Passenger>();

  isLoading = false;
  errorMessage = '';

  onInputChange(event: Event) {
    const value = (event.target as HTMLInputElement).value;
    this.value = value;
    this.valueChange.emit(value);
    this.errorMessage = ''; // Clear error on input change
  }

  async onAddPassanger() {
    console.log('onAddPassanger called, value:', this.value);
    
    // Basic validation
    if (!this.value.trim()) {
      this.errorMessage = 'Please enter a passenger name or email';
      console.log('Empty value');
      return;
    }

    this.isLoading = true;
    this.errorMessage = '';

    try {
      // Create passenger from input
      const passenger: Passenger = {
        id: Date.now().toString(),
        email: this.value,
        name: this.value // Use the input as name for now
      };
      
      console.log('Emitting passenger:', passenger);
      this.passengerAdded.emit(passenger);
      
      // Clear input after successful add
      this.value = '';
      this.valueChange.emit('');
    } catch (error) {
      console.error('Error adding passenger:', error);
      this.errorMessage = 'Failed to add passenger. Please try again.';
    } finally {
      this.isLoading = false;
    }
  }

  private isValidEmail(email: string): boolean {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
  }

  private async checkPassengerExists(email: string): Promise<Passenger | null> {
    // TODO: Replace this with your actual API call
    // Example:
    // const response = await this.http.get<Passenger>(`/api/passengers/${email}`).toPromise();
    // return response;
    
    // Placeholder - simulate API call
    return new Promise((resolve) => {
      setTimeout(() => {
        // Simulate successful response
        resolve({
          id: Date.now().toString(),
          email: email,
          name: 'John Doe' // This would come from your API
        });
      }, 1000);
    });
  }

  onFocus() {
    this.errorMessage = '';
  }
}
