import {
  ApplicationRef,
  ComponentRef,
  createComponent,
  EnvironmentInjector,
  Injectable,
} from '@angular/core';
import { RideScheduleData, ScheduleRideDialog } from '@app/features/find-trip/schedule-ride-dialog/schedule-ride-dialog';
import { ErrorDialog } from '@app/shared/components/error-dialog/error-dialog';
import { SuccessDialog } from '@app/shared/components/success-dialog/success-dialog';
import { Subject } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class DialogService {
  private dialogRef?: ComponentRef<ErrorDialog | SuccessDialog>;
  private scheduleDialogRef?: ComponentRef<ScheduleRideDialog>;

  constructor(
    private appRef: ApplicationRef,
    private injector: EnvironmentInjector,
  ) {}

  open(title: string, message: string, isError: boolean = true) {
    if (this.dialogRef) return;

    this.dialogRef = createComponent(isError ? ErrorDialog : SuccessDialog, {
      environmentInjector: this.injector,
    });

    this.dialogRef.instance.title = title;
    this.dialogRef.instance.message = message;

    this.dialogRef.instance.closed.subscribe(() => this.close());

    this.appRef.attachView(this.dialogRef.hostView);

    document.body.appendChild(this.dialogRef.location.nativeElement);
  }

  close() {
    if (!this.dialogRef) return;

    this.appRef.detachView(this.dialogRef.hostView);
    this.dialogRef.destroy();
    this.dialogRef = undefined;
  }

  openScheduleRide(): Subject<RideScheduleData> {
    const resultSubject = new Subject<RideScheduleData>();

    if (this.scheduleDialogRef) {
      return resultSubject;
    }

    this.scheduleDialogRef = createComponent(ScheduleRideDialog, {
      environmentInjector: this.injector,
    });

    this.scheduleDialogRef.instance.result.subscribe((data: RideScheduleData) => {
      resultSubject.next(data);
      resultSubject.complete();
      this.closeScheduleRide();
    });

    this.scheduleDialogRef.instance.cancel.subscribe(() => {
      resultSubject.complete();
      this.closeScheduleRide();
    });

    this.appRef.attachView(this.scheduleDialogRef.hostView);

    document.body.appendChild(this.scheduleDialogRef.location.nativeElement);

    return resultSubject;
  }

  closeScheduleRide() {
    if (!this.scheduleDialogRef) return;

    this.appRef.detachView(this.scheduleDialogRef.hostView);
    this.scheduleDialogRef.destroy();
    this.scheduleDialogRef = undefined;
  }
}
