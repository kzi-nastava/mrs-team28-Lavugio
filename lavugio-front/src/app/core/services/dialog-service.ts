import { ApplicationRef, ComponentRef, createComponent, EnvironmentInjector, Injectable } from '@angular/core';
import { ErrorDialog } from '@app/shared/components/error-dialog/error-dialog';
import { SuccessDialog } from '@app/shared/components/success-dialog/success-dialog';

@Injectable({
  providedIn: 'root',
})
export class DialogService {
  private dialogRef?: ComponentRef<ErrorDialog | SuccessDialog>;

  constructor(
    private appRef: ApplicationRef,
    private injector: EnvironmentInjector
  ) {}

  open(title: string, message: string, isError: boolean = true) {
    if (this.dialogRef) return;

    this.dialogRef = createComponent(isError ? ErrorDialog : SuccessDialog, {
      environmentInjector: this.injector
    });

    this.dialogRef.instance.title = title;
    this.dialogRef.instance.message = message;

    this.dialogRef.instance.closed.subscribe(() => this.close());

    this.appRef.attachView(this.dialogRef.hostView);

    document.body.appendChild(
      this.dialogRef.location.nativeElement
    );
  }

  close() {
    if (!this.dialogRef) return;

    this.appRef.detachView(this.dialogRef.hostView);
    this.dialogRef.destroy();
    this.dialogRef = undefined;
  }
}
