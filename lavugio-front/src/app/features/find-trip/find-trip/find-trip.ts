import {
  ChangeDetectorRef,
  Component,
  effect,
  OnDestroy,
  OnInit,
  signal,
  ViewChild,
} from '@angular/core';
import { FormBackgroundSheet } from '@app/features/form-background-sheet/form-background-sheet';
import { Navbar } from '@app/shared/components/navbar/navbar';
import { MapComponent } from '@app/shared/components/map/map';
import { DestinationSelector } from '../destination-selector/destination-selector';
import { DestinationsDisplay } from '../destinations-display/destinations-display';
import { Subject, takeUntil } from 'rxjs';
import { WizardStateService } from '../wizard-state-service';
import { GeocodeResult, GeocodingService } from '../geocoding-service/geocoding-service';
import { PreferencesSelect } from '../preferences-select/preferences-select';
import { Passenger } from '../add-passanger-input/add-passanger-input';
import { TripSummary } from '../trip-summary/trip-summary';
import { TripDestination } from '@app/shared/models/tripDestination';
import { MarkerIcons } from '@app/shared/components/map/marker-icons';
import { FavoriteRoutesDialog } from '../favorite-routes-dialog/favorite-routes-dialog';
import { FavoriteRoute } from '@app/shared/models/favoriteRoute';
import { Coordinates } from '@app/shared/models/coordinates';
import { DialogService } from '@app/core/services/dialog-service';
import { FavoriteRouteService } from '@app/core/services/route/favorite-route-service';
import { NewFavoriteRouteRequest } from '@app/shared/models/route/newFavoriteRouteRequest';
import { RideScheduleData } from '../schedule-ride-dialog/schedule-ride-dialog';
import { RouteEstimateInfo } from '@app/shared/models/route/routeEstimateInfo';
import { RideService } from '@app/core/services/ride-service';
import { RideEstimateRequest } from '@app/shared/models/ride/rideEstimateRequest';
import { UserService } from '@app/core/services/user/user-service';
import { RideRequestDTO } from '@app/shared/models/ride/rideRequestDTO';
import { VehicleType } from '@app/shared/models/vehicleType';

@Component({
  selector: 'app-find-trip',
  imports: [
    Navbar,
    FormBackgroundSheet,
    MapComponent,
    DestinationSelector,
    DestinationsDisplay,
    PreferencesSelect,
    TripSummary,
    FavoriteRoutesDialog,
  ],
  templateUrl: './find-trip.html',
  styleUrl: './find-trip.css',
})
export class FindTrip implements OnInit, OnDestroy {
  isPanelOpen: boolean = false;

  togglePanel() {
    this.isPanelOpen = !this.isPanelOpen;
  }

  @ViewChild('map') map!: MapComponent;
  @ViewChild('destinationSelector') destinationSelector!: DestinationSelector;
  isMapPickMode = false;

  destinations: TripDestination[] = [];

  scheduleData = signal<RideScheduleData | null>(null);

  currentStep = 0;
  totalSteps = 0;
  currentTitle = '';

  passengers: Passenger[] = [];
  selectedVehicleType = '';
  isPetFriendly = false;
  isBabyFriendly = false;
  canFinishTrip = false;
  favoriteRouteName: string = '';

  rideEstimate = signal<RouteEstimateInfo | null>(null);
  ridePrice = signal<number>(0);

  private destroy$ = new Subject<void>();

  constructor(
    public wizardState: WizardStateService,
    private dialogService: DialogService,
    private favoriteRouteService: FavoriteRouteService,
    private geocodingService: GeocodingService,
    private rideService: RideService,
    private cdr: ChangeDetectorRef,
    private userService: UserService,
  ) {}

  ngOnInit() {
    this.totalSteps = this.wizardState.getTotalSteps();

    this.wizardState.currentStepIndex$.pipe(takeUntil(this.destroy$)).subscribe((index) => {
      this.currentStep = index;
      this.currentTitle = this.wizardState.getStepInfo(index).title;
    });

    const savedDestinations = this.wizardState.getStepData('destinations');
    if (savedDestinations) {
      this.destinations = savedDestinations;
    }

    const savedPreferences = this.wizardState.getStepData('preferences');
    if (savedPreferences) {
      this.selectedVehicleType = savedPreferences.vehicleType || '';
      this.isPetFriendly = savedPreferences.isPetFriendly || false;
      this.isBabyFriendly = savedPreferences.isBabyFriendly || false;
    }
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }

  onDestinationAdded(geocodeResult: GeocodeResult) {
    // Convert GeocodeResult to Destination and add to list
    const newDestination: TripDestination = {
      id: geocodeResult.place_id?.toString() || crypto.randomUUID(),
      name: geocodeResult.display_name,
      street: geocodeResult.street || '',
      houseNumber: geocodeResult.streetNumber || '',
      city: geocodeResult.city || '',
      country: geocodeResult.country || '',
      coordinates: {
        latitude: Number(geocodeResult.lat),
        longitude: Number(geocodeResult.lon),
      },
    };

    this.destinations.push(newDestination);
    this.map.addMarker(newDestination.coordinates, MarkerIcons.checkpoint);
    this.updateRoute();

    // Recalculate estimate if on review step
    if (this.isLastStep()) {
      this.loadRideEstimate();
    }
  }

  onDestinationRemoved(destinationId: string) {
    this.destinations = this.destinations.filter((d) => d.id !== destinationId);

    this.map.resetMarkers();
    this.map.removeRoute();

    // Re-add markers in correct order
    this.destinations.forEach((d, index) => {
      const icon =
        index === 0
          ? MarkerIcons.start
          : index === this.destinations.length - 1
            ? MarkerIcons.end
            : MarkerIcons.checkpoint;

      this.map.addMarker(d.coordinates, icon);
    });

    this.updateRoute();

    // Recalculate estimate if on review step
    if (this.isLastStep()) {
      this.loadRideEstimate();
    }
  }

  private updateRoute() {
    if (this.destinations.length < 2) return;

    const coords = this.destinations.map((d) => d.coordinates);
    this.map.setRoute(coords);
  }

  onPassengerAdded(passenger: Passenger) {
    if (this.passengers.find((p) => p.email === passenger.email)) {
      this.dialogService.open(
        'Duplicate Passenger',
        'This passenger has already been added.',
        true,
      );
      return;
    } else {
      passenger.email = passenger.email.toLocaleLowerCase();
      this.passengers = [...this.passengers, passenger];
    }
  }

  onPassengerRemoved(passengerId: string) {
    this.passengers = this.passengers.filter((p) => p.id !== passengerId);
  }

  onCanFinishChange(canFinish: boolean) {
    this.canFinishTrip = canFinish;
  }

  onPreferencesChanged(preferences: {
    vehicleType: string;
    isPetFriendly: boolean;
    isBabyFriendly: boolean;
  }) {
    this.selectedVehicleType = preferences.vehicleType;
    this.isPetFriendly = preferences.isPetFriendly;
    this.isBabyFriendly = preferences.isBabyFriendly;

    // Recalculate price if on review step and vehicle type changed
    if (this.isLastStep() && this.destinations.length >= 2) {
      this.loadRideEstimate();
    }
  }

  openScheduleDialog(tripData: {
    destinations: TripDestination[];
    passengers: Passenger[];
    preferences: {
      vehicleType: string;
      isPetFriendly: boolean;
      isBabyFriendly: boolean;
    };
  }) {
    this.dialogService.openScheduleRide().subscribe({
      next: (result) => {
        console.log('Schedule data:', result);
        this.scheduleData.set(result);

        const rideRequest: RideRequestDTO = {
          destinations: this.destinations.map((d, index) => ({
            location: {
              orderIndex: index,
              latitude: d.coordinates.latitude,
              longitude: d.coordinates.longitude,
            },
            address: d.name,
            streetName: d.street,
            city: d.city,
            country: d.country,
            streetNumber: parseInt(d.houseNumber) || 0,
            zipCode: 0,
          })),
          passengerEmails: this.passengers.map((p) => p.email),
          vehicleType: this.selectedVehicleType as VehicleType,
          babyFriendly: this.isBabyFriendly,
          petFriendly: this.isPetFriendly,
          scheduledTime: result.scheduledTime ? new Date(result.scheduledTime).toISOString().replace('Z', '') : '',
          scheduled: result.isScheduled,
        };

        console.log('Creating ride with:', rideRequest);
        // TODO: Call ride service with rideRequest
      },
      complete: () => {
        console.log('Modal closed');
      },
    });
  }

  onFinish() {
    this.saveCurrentStepData();
    this.userService.canUserOrderRide().subscribe({
      next: (response) => {
        console.log(response);
        if (response.block.blocked) {
          this.dialogService.openBlocked(response.block.reason);
        } else if (response.inRide) {
          this.dialogService.open(
            'Cannot Order Ride',
            'You are already in an active ride and cannot order a new one.',
            true,
          );
        } else {
          const tripData = {
            destinations: this.destinations,
            passengers: this.passengers.map((p) => {
              p.email = p.email.toLowerCase();
              return p;
            }),
            preferences: {
              vehicleType: this.selectedVehicleType,
              isPetFriendly: this.isPetFriendly,
              isBabyFriendly: this.isBabyFriendly,
            },
          };
          console.log('Trip submitted with:', tripData);
          this.openScheduleDialog(tripData);
        }
      },
      error: (error) => {
        console.error('Error checking if user is blocked:', error);
        this.dialogService.open(
          'Error',
          'Unable to verify user status. Please try again later.',
          true,
        );
      },
    });
  }

  loadRideEstimate() {
    if (this.destinations.length < 2) {
      this.rideEstimate.set(null);
      this.ridePrice.set(0);
      return;
    }

    const coordinates: Coordinates[] = this.destinations.map((d) => d.coordinates);
    this.geocodingService.getRouteInfo(coordinates).subscribe({
      next: (routeInfo) => {
        if (!routeInfo) {
          console.error('Failed to get route info');
          return;
        }
        const routeEstimate: RouteEstimateInfo = {
          distanceMeters: routeInfo.distanceMeters,
          durationSeconds: routeInfo.durationSeconds,
        };

        this.rideEstimate.set(routeEstimate);

        if (this.selectedVehicleType !== '') {
          const request: RideEstimateRequest = {
            distanceMeters: routeEstimate.distanceMeters,
            selectedVehicleType: this.selectedVehicleType,
          };

          this.rideService.getPriceForRide(request).subscribe({
            next: (priceResponse) => {
              console.log('priceResponse', priceResponse);
              this.ridePrice.set(priceResponse);
            },
            error: (error) => {
              console.error('Failed to calculate price:', error);
              this.dialogService.open(
                'Price Calculation Failed',
                'Unable to calculate ride price at this time.',
                true,
              );
            },
          });
        }
      },
      error: (error) => {
        console.error('Failed to get route info:', error);
      },
    });
  }

  onNext() {
    this.saveCurrentStepData();
    this.wizardState.nextStep();

    if (this.isLastStep()) {
      this.loadRideEstimate();
    }
  }

  onPrevious() {
    this.saveCurrentStepData();
    this.wizardState.previousStep();
  }

  saveCurrentStepData() {
    const stepInfo = this.wizardState.getStepInfo(this.currentStep);

    switch (stepInfo.id) {
      case 'destinations':
        const destinationData = {
          // destinations data
        };
        this.wizardState.saveStepData('destinations', destinationData);
        break;
      case 'preferences':
        this.wizardState.saveStepData('preferences', {
          vehicleType: this.selectedVehicleType,
          isPetFriendly: this.isPetFriendly,
          isBabyFriendly: this.isBabyFriendly,
        });
        break;
      case 'review':
        break;
    }
  }

  showFavoritesDialog = false;

  favoriteRoutes: FavoriteRoute[] = [];

  getFavoriteRoutes() {
    this.favoriteRouteService.getFavoriteRoutes().subscribe({
      next: (response) => {
        this.favoriteRoutes = response;
      },
      error: (err) => {
        this.dialogService.open('Loading Favorite Routes Failed', err.error.message, true);
      },
    });
  }

  private generateDisplayName(destination: {
    street?: string;
    houseNumber?: string;
    city?: string;
    country?: string;
  }): string {
    let displayName = '';

    if (destination.street) {
      displayName = destination.street;

      if (destination.houseNumber) {
        displayName += ' ' + destination.houseNumber;
      }

      if (destination.city) {
        displayName += ', ' + destination.city;
      }
    } else if (destination.city) {
      displayName = destination.city;
    } else {
      displayName = 'Unknown location';
    }

    return displayName;
  }

  enableMapPickMode() {
    this.isMapPickMode = true;
  }

  onMapClicked(coords: Coordinates) {
    if (this.isMapPickMode) {
      this.onMapPicked(coords);
    }
  }

  onMapPicked(coords: Coordinates) {
    this.destinationSelector.setLocationFromMap(coords);

    this.isMapPickMode = false;

    this.map.clickedLocation.set(null);
  }

  openFavorites() {
    this.favoriteRouteService.getFavoriteRoutes().subscribe({
      next: (response) => {
        this.favoriteRoutes = response.map((route: FavoriteRoute) => ({
          ...route,
          destinations: route.destinations.map((d) => ({
            ...d,
            name: this.generateDisplayName(d),
          })),
        }));
        this.showFavoritesDialog = true;
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.dialogService.open('Loading Favorite Routes Failed', err.error.message, true);
      },
    });
  }

  closeFavorites() {
    this.showFavoritesDialog = false;
  }

  applyFavoriteRoute(route: FavoriteRoute) {
    this.destinations = route.destinations.map((d) => ({
      id: crypto.randomUUID(),
      name: this.generateDisplayName(d),
      street: d.street,
      houseNumber: d.houseNumber,
      city: d.city,
      country: d.country,
      coordinates: {
        latitude: d.coordinates.latitude,
        longitude: d.coordinates.longitude,
      },
    }));

    this.showFavoritesDialog = false;

    this.updateRoute();
  }

  saveFavoriteRoute() {
    if (this.destinations.length < 2) {
      this.dialogService.open(
        'Cannot save route',
        'Please add at least two destinations to save a favorite route.',
        true,
      );
      return;
    }

    if (!this.favoriteRouteName.trim()) {
      this.dialogService.open(
        'Cannot save route',
        'Please enter a name for the favorite route.',
        true,
      );
      return;
    }

    const newFavoriteRoute: NewFavoriteRouteRequest = {
      name: this.favoriteRouteName,
      destinations: this.destinations,
    };

    this.favoriteRouteService.saveFavoriteRoute(newFavoriteRoute).subscribe({
      next: (respond) => {
        this.dialogService.open(
          'Route saved',
          'Your favorite route has been saved successfully.',
          false,
        );
        setTimeout(() => {
          this.favoriteRouteName = '';
        });
      },
      error: (err) => {
        this.dialogService.open('Adding Favorite Route Failed', err.error.message, true);
      },
    });
  }

  isFirstStep(): boolean {
    return this.currentStep === 0;
  }

  isLastStep(): boolean {
    return this.currentStep === this.totalSteps - 1;
  }

  getNextButtonText(): string {
    return this.isLastStep() ? 'Finish' : 'Next';
  }

  getPreviousButtonText(): string {
    return 'Previous';
  }
}
