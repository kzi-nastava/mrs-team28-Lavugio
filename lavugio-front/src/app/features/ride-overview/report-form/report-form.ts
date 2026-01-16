import { Component, inject, OnDestroy, output, Signal, signal, WritableSignal } from '@angular/core';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { RideService } from '@app/core/services/ride-service';
import { RideReport } from '@app/shared/models/rideReport';

@Component({
  selector: 'app-report-form',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './report-form.html',
})
export class ReportForm implements OnDestroy{

  private allowedRegex = /[^a-zA-Z0-9 .,!?-]/g;
  private rideService = inject(RideService);

  isDone: WritableSignal<Boolean> = signal(false);
  isHidden: WritableSignal<Boolean> = signal(false);
  isFailed: WritableSignal<Boolean> = signal(false);
  isLoading: WritableSignal<Boolean> = signal(false);
  rideId: number = 0;

  hideReportOutput = output();

  reportControl = new FormControl('', {
    nonNullable: true
  });

  readonly maxLength = 256;

  onInput() {
    
    const value = this.reportControl.value;

    this.reportControl.setValue(
      value.replace(this.allowedRegex, '').slice(0, this.maxLength),
      { emitEvent: false }
    );
  }

  sendReport(){
    this.isLoading.set(true);
    let report: RideReport = {
      rideId: this.rideId,
      comment:this.reportControl.value
    }
    this.rideService.postRideReport(this.rideId, report).subscribe({
      next: () =>{
        console.log("Report successful")
        this.isDone.set(true);
        this.isLoading.set(false);
      },
      error: () => {
        console.error("Report failed")
        this.isFailed.set(true);
        this.isLoading.set(false);
      }
    });
  }

  ngOnDestroy(){
    this.hideReportOutput.emit();
  }

  onEnterPress(event: Event) {

    if (this.isLoading()) return;

    if (!(event instanceof KeyboardEvent)) return;

    const value = this.reportControl.value?.trim();

    if (!event.shiftKey && value) {
      event.preventDefault();
      this.sendReport();
    }
  }
}
