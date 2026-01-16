import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

export interface WizardState {
  id: string;
  title: string;
  data?: any;
}

@Injectable({
  providedIn: 'root',
})
export class WizardStateService {
  private currentStepIndex = new BehaviorSubject<number>(0);
  private stepsData = new BehaviorSubject<Map<string, any>>(new Map());

  currentStepIndex$ = this.currentStepIndex.asObservable();
  stepData$ = this.stepsData.asObservable();

  private steps: WizardState[] = [
    {id: "destinations", title: "Find a Trip"},
    {id: "preferences", title:"Preferences"},
    {id: "review", title: "Review & Confirm"}
  ];

  getCurrentStep(): number {
    return this.currentStepIndex.value;
  }

  getTotalSteps(): number {
    return this.steps.length;
  }

  getStepInfo(index: number): WizardState {
    return this.steps[index];
  }

  nextStep(): void {
    const current = this.currentStepIndex.value;
    if (current < this.steps.length - 1) {
      this.currentStepIndex.next(current + 1);
    }
  }

  previousStep(): void {
    const current = this.currentStepIndex.value;
    if (current > 0) {
      this.currentStepIndex.next(current - 1);
    }
  }

  saveStepData(stepId: string, data: any): void {
    const currentData = this.stepsData.value;
    currentData.set(stepId, data);
    this.stepsData.next(currentData);
  }

  getStepData(stepId: string): any {
    return this.stepsData.value.get(stepId);
  }

  reset(): void {
    this.currentStepIndex.next(0);
    this.stepsData.next(new Map());
  }
}
