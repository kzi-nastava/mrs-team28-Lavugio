import { ApplicationRef, ComponentRef, createComponent, EnvironmentInjector, Injectable } from '@angular/core';
import { ErrorDialog } from '@app/shared/components/error-dialog/error-dialog';

@Injectable({
  providedIn: 'root',
})
export class ErrorDialogService {
  private dialogRef?: ComponentRef<ErrorDialog>;

  constructor(
    private appRef: ApplicationRef,
    private injector: EnvironmentInjector
  ) {}

  open(title: string, message: string) {
    if (this.dialogRef) return;

    this.dialogRef = createComponent(ErrorDialog, {
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
