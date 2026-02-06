import { Component, inject, output, signal } from '@angular/core';
import { FormControl, ReactiveFormsModule, Validators } from '@angular/forms';
import { RidePriceModel } from '@app/shared/models/ridePrice';
import { PriceDefinitionService } from '@app/core/services/price-definition-service';

@Component({
  selector: 'app-price-definition',
  imports: [ReactiveFormsModule],
  templateUrl: './price-definition.html',
  styleUrl: './price-definition.css',
})
export class PriceDefinitionComponent {

  private priceDefinitionService = inject(PriceDefinitionService);

  private vehiclePrices: RidePriceModel = {
    'standard': 0,
    'luxury': 0,
    'combi': 0,
    'kilometer': 0
  };

  pricePerKmControl = new FormControl(0, [Validators.required, Validators.min(0)]);
  hideFormOutput = output();
  isDone = signal<boolean>(false);
  vehicleTypeControl = new FormControl<keyof RidePriceModel>('standard');
  pricePerTypeControl = new FormControl(0, [Validators.required, Validators.min(0)]);
  isFailed = signal<boolean>(false);
  isLoadingPrices = signal<boolean>(false);  // Za učitavanje sa back-a
  isSaving = signal<boolean>(false);

  ngOnInit() {
    this.loadPrices();

    // Čuvaj cenu trenutnog tipa pre promene
    this.vehicleTypeControl.valueChanges.subscribe(value => {
      if (value && value !== 'kilometer') {
        // Sačuvaj trenutnu vrednost pre promene
        const previousType = this.getPreviousVehicleType();
        const currentPrice = this.pricePerTypeControl.value;
        if (previousType && currentPrice !== null) {
          this.vehiclePrices[previousType] = currentPrice;
        }

        // Učitaj cenu za novi tip
        const newPrice = this.vehiclePrices[value];
        this.pricePerTypeControl.setValue(newPrice);
      }
    });

    // Čuvaj promene cene po kilometru u realnom vremenu
    this.pricePerKmControl.valueChanges.subscribe(value => {
      if (value !== null) {
        this.vehiclePrices['kilometer'] = value;
      }
    });

    // Čuvaj promene cene tipa vozila u realnom vremenu
    this.pricePerTypeControl.valueChanges.subscribe(value => {
      const currentType = this.vehicleTypeControl.value;
      if (currentType && currentType !== 'kilometer' && value !== null) {
        this.vehiclePrices[currentType] = value;
      }
    });
  }

  private previousVehicleType: keyof RidePriceModel | null = 'standard';

  private getPreviousVehicleType(): keyof RidePriceModel | null {
    const current = this.previousVehicleType;
    this.previousVehicleType = this.vehicleTypeControl.value;
    return current;
  }

  hideForm() {
    this.hideFormOutput.emit();
  }

  loadPrices() {
    this.isLoadingPrices.set(true); 
    this.priceDefinitionService.getPrices().subscribe({
      next: (prices) => {
        this.vehiclePrices = prices;
        this.pricePerKmControl.setValue(prices.kilometer);
        
        const currentType = this.vehicleTypeControl.value;
        if (currentType && currentType !== 'kilometer') {
          this.pricePerTypeControl.setValue(prices[currentType]);
          this.previousVehicleType = currentType;
        }
        
        this.isLoadingPrices.set(false);
        this.isFailed.set(false);
      },
      error: (error) => {
        console.error('Error loading prices:', error);
        this.isLoadingPrices.set(false);
        this.isFailed.set(true);
      }
    });
  }

  save() {
    const selectedVehicleType = this.vehicleTypeControl.value;
    const vehiclePrice = this.pricePerTypeControl.value;
    const pricePerKm = this.pricePerKmControl.value;
    
    if (selectedVehicleType && vehiclePrice !== null && pricePerKm !== null) {
      this.isSaving.set(true); 
      this.isFailed.set(false);

      this.vehiclePrices[selectedVehicleType] = vehiclePrice;
      this.vehiclePrices['kilometer'] = pricePerKm;
      
      this.priceDefinitionService.postPrices(this.vehiclePrices).subscribe({
        next: () => {
          this.isSaving.set(false);
          this.isDone.set(true);
        },
        error: (error) => {
          console.error('Error saving prices:', error);
          this.isSaving.set(false);
          this.isFailed.set(true);
        }
      });
    }
  }
}