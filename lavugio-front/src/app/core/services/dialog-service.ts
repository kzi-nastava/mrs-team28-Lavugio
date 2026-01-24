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
import { ConfirmDialog } from '@app/shared/components/confirm-dialog/confirm-dialog';
import { Observable, Subject } from 'rxjs';

export class DialogRef {
  constructor(private closeSubject: Subject<void>) {}

  afterClosed(): Observable<void> {
    return this.closeSubject.asObservable();
  }
}

@Injectable({
  providedIn: 'root',
})
export class DialogService {
  private dialogRef?: ComponentRef<ErrorDialog | SuccessDialog>;
  private confirmDialogRef?: ComponentRef<ConfirmDialog>;
  private scheduleDialogRef?: ComponentRef<ScheduleRideDialog>;
  private closeSubject?: Subject<void>;

  constructor(
    private appRef: ApplicationRef,
    private injector: EnvironmentInjector,
  ) {}

  open(title: string, message: string, isError: boolean = true): DialogRef {
    if (this.dialogRef) return new DialogRef(new Subject<void>());

    this.closeSubject = new Subject<void>();

    this.dialogRef = createComponent(isError ? ErrorDialog : SuccessDialog, {
      environmentInjector: this.injector,
    });

    this.dialogRef.instance.title = title;
    this.dialogRef.instance.message = message;

    this.dialogRef.instance.closed.subscribe(() => this.close());

    this.appRef.attachView(this.dialogRef.hostView);

    document.body.appendChild(this.dialogRef.location.nativeElement);

    return new DialogRef(this.closeSubject);
  }

  close() {
    if (!this.dialogRef) return;

    this.closeSubject?.next();
    this.closeSubject?.complete();

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

  openConfirm(title: string, message: string): Subject<boolean> {
    const resultSubject = new Subject<boolean>();

    if (this.confirmDialogRef) {
      return resultSubject;
    }

    this.confirmDialogRef = createComponent(ConfirmDialog, {
      environmentInjector: this.injector,
    });

    this.confirmDialogRef.instance.title = title;
    this.confirmDialogRef.instance.message = message;

    this.confirmDialogRef.instance.result.subscribe((confirmed: boolean) => {
      resultSubject.next(confirmed);
      resultSubject.complete();
      this.closeConfirm();
    });

    this.appRef.attachView(this.confirmDialogRef.hostView);

    document.body.appendChild(this.confirmDialogRef.location.nativeElement);

    return resultSubject;
  }

  closeConfirm() {
    if (!this.confirmDialogRef) return;

    this.appRef.detachView(this.confirmDialogRef.hostView);
    this.confirmDialogRef.destroy();
    this.confirmDialogRef = undefined;
  }
}
