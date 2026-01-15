import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { FormBackgroundSheet } from '@app/features/form-background-sheet/form-background-sheet';
import { Navbar } from '@app/shared/components/navbar/navbar';
import { MapComponent } from "@app/shared/components/map/map";
import { DestinationSelector } from "../destination-selector/destination-selector";
import { DestinationsDisplay } from "../destinations-display/destinations-display";
import { Subject, takeUntil } from 'rxjs';
import { WizardStateService } from '../wizard-state-service';
import { GeocodeResult } from '../geocoding-service/geocoding-service';
import { Destination } from '@app/shared/models/destination';
import { PreferencesSelect } from "../preferences-select/preferences-select";
import { Passenger } from "../add-passanger-input/add-passanger-input";
import { TripSummary } from "../trip-summary/trip-summary";
import { TripStatsDisplay } from "../trip-stats-display/trip-stats-display";
import { TripDestination } from '@app/shared/models/tripDestination';
import { MarkerIcons } from '@app/shared/components/map/marker-icons';

@Component({
  selector: 'app-find-trip',
  imports: [Navbar, FormBackgroundSheet, MapComponent, DestinationSelector, DestinationsDisplay, PreferencesSelect, TripSummary, TripStatsDisplay],
  templateUrl: './find-trip.html',
  styleUrl: './find-trip.css',
})
export class FindTrip implements OnInit, OnDestroy {
  isPanelOpen: boolean = false;

  togglePanel() {
    this.isPanelOpen = !this.isPanelOpen;
  }

  @ViewChild('map') map!: MapComponent;
  destinations: TripDestination[] = [];

  currentStep = 0;
  totalSteps = 0;
  currentTitle = "";

  passengers: Passenger[] = [];
  selectedVehicleType = '';
  isPetFriendly = false;
  isBabyFriendly = false;
  canFinishTrip = false;

  // Trip stats
  tripDistance = '0km';
  tripEstimatedTime = '0min';
  tripPrice = '0$';

  private destroy$ = new Subject<void>();

  constructor(public wizardState: WizardStateService) {}

  ngOnInit() {
    this.totalSteps = this.wizardState.getTotalSteps();
    
    this.wizardState.currentStepIndex$
      .pipe(takeUntil(this.destroy$))
      .subscribe(index => {
        this.currentStep = index;
        this.currentTitle = this.wizardState.getStepInfo(index).title;
      })

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
      id: crypto.randomUUID(), // or use geocodeResult.place_id if available
      name: geocodeResult.display_name,
      coordinates: {
        latitude: Number(geocodeResult.lat),
        longitude: Number(geocodeResult.lon)
      }
    };

    this.destinations.push(newDestination);
    this.map.addMarker(newDestination.coordinates, MarkerIcons.checkpoint);
    this.updateRoute();
  }

  onDestinationRemoved(destinationId: string) {
    this.destinations = this.destinations.filter(d => d.id !== destinationId);

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
  }

  private updateRoute() {
    if (this.destinations.length < 2) return;

    const coords = this.destinations.map(d => d.coordinates);
    this.map.setRoute(coords);
  }

  onPassengerAdded(passenger: Passenger) {
    console.log('FindTrip - onPassengerAdded:', passenger);
    this.passengers = [...this.passengers, passenger];
    console.log('FindTrip - passengers array:', this.passengers);
  }

  onPassengerRemoved(passengerId: string) {
    console.log('FindTrip - onPassengerRemoved:', passengerId);
    this.passengers = this.passengers.filter(p => p.id !== passengerId);
    console.log('FindTrip - passengers array:', this.passengers);
  }

  onCanFinishChange(canFinish: boolean) {
    this.canFinishTrip = canFinish;
  }

  onPreferencesChanged(preferences: {vehicleType: string, isPetFriendly: boolean, isBabyFriendly: boolean}) {
    this.selectedVehicleType = preferences.vehicleType;
    this.isPetFriendly = preferences.isPetFriendly;
    this.isBabyFriendly = preferences.isBabyFriendly;
  }

  onFinish() {
    this.saveCurrentStepData();
    console.log('Trip submitted with:', {
      destinations: this.destinations,
      passengers: this.passengers,
      preferences: {
        vehicleType: this.selectedVehicleType,
        isPetFriendly: this.isPetFriendly,
        isBabyFriendly: this.isBabyFriendly
      }
    });
    // Handle trip submission here
  }

  onNext() {
    this.saveCurrentStepData();
    this.wizardState.nextStep();
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
          isBabyFriendly: this.isBabyFriendly
        });
        break;
      case "review":
        break;
    }
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