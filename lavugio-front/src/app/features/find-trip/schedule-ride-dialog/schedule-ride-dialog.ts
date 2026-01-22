import { Component, computed, output, signal } from '@angular/core';

export interface RideScheduleData {
  isScheduled: boolean;
  scheduledTime: Date | null;
}

@Component({
  selector: 'app-schedule-ride-dialog',
  imports: [],
  templateUrl: './schedule-ride-dialog.html',
  styleUrl: './schedule-ride-dialog.css',
})
export class ScheduleRideDialog {
  isScheduled = signal(false);
  selectedTime = signal<string>('');

  result = output<RideScheduleData>();
  cancel = output<void>();

  minTime = computed(() => {
    const now = new Date();
    const hours = now.getHours().toString().padStart(2, '0');
    const minutes = now.getMinutes().toString().padStart(2, '0');
    return `${hours}:${minutes}`;
  });

  maxTime = computed(() => {
    const now = new Date();
    const maxDate = new Date(now.getTime() + 5 * 60 * 60 * 1000); // +5 sati
    const hours = maxDate.getHours().toString().padStart(2, '0');
    const minutes = maxDate.getMinutes().toString().padStart(2, '0');
    return `${hours}:${minutes}`;
  });

  isValid = computed(() => {
    if (!this.isScheduled()) {
      return true;
    }
    
    const timeValue = this.selectedTime().trim();
    if (timeValue === '') {
      return false;
    }

    // Check if the scheduled time is within 5 hours
    const [hours, minutes] = timeValue.split(':');
    const now = new Date();
    let scheduledTime = new Date(
      now.getFullYear(),
      now.getMonth(),
      now.getDate(),
      parseInt(hours),
      parseInt(minutes),
    );

    // If the time is in the past, it means tomorrow
    if (scheduledTime < now) {
      scheduledTime.setDate(scheduledTime.getDate() + 1);
    }

    const maxAllowedTime = new Date(now.getTime() + 5 * 60 * 60 * 1000);
    return scheduledTime <= maxAllowedTime;
  });

  getRideScheduleData(): RideScheduleData {
    let scheduledTime: Date | null = null;

    if (this.isScheduled() && this.selectedTime()) {
      const [hours, minutes] = this.selectedTime().split(':');
      const now = new Date();
      scheduledTime = new Date(
        now.getFullYear(),
        now.getMonth(),
        now.getDate(),
        parseInt(hours),
        parseInt(minutes),
      );

      if (scheduledTime < now) {
        scheduledTime.setDate(scheduledTime.getDate() + 1);
      }
    }

    return {
      isScheduled: this.isScheduled(),
      scheduledTime: scheduledTime,
    };
  }

  selectRideNow() {
    this.isScheduled.set(false);
    this.selectedTime.set('');
  }

  selectScheduleRide() {
    this.isScheduled.set(true);
    this.selectedTime.set(this.minTime());
  }

  onCancel() {
    this.cancel.emit();
  }

  onSchedule() {
    if (!this.isValid()) return;

    let scheduledTime: Date | null = null;

    if (this.isScheduled() && this.selectedTime()) {
      const [hours, minutes] = this.selectedTime().split(':');
      const now = new Date();
      scheduledTime = new Date(
        now.getFullYear(),
        now.getMonth(),
        now.getDate(),
        parseInt(hours),
        parseInt(minutes),
      );

      if (scheduledTime < now) {
        scheduledTime.setDate(scheduledTime.getDate() + 1);
      }
    }

    this.result.emit({
      isScheduled: this.isScheduled(),
      scheduledTime: scheduledTime,
    });
  }
}
