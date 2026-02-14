import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of, throwError } from 'rxjs';
import { provideRouter } from '@angular/router';

import { RegisterDriver } from './register-driver';
import { DriverService } from '@app/core/services/user/driver-service';
import { DialogService } from '@app/core/services/dialog-service';
import { Router } from '@angular/router';

describe('RegisterDriver', () => {
  let component: RegisterDriver;
  let fixture: ComponentFixture<RegisterDriver>;

  let driverServiceMock: any;
  let dialogServiceMock: any;
  let router: Router;

  beforeEach(async () => {
    driverServiceMock = {
      registerDriver: jasmine.createSpy().and.returnValue(of({})),
    };

    dialogServiceMock = {
      open: jasmine.createSpy(),
    };

    await TestBed.configureTestingModule({
      imports: [RegisterDriver],
      providers: [
        provideRouter([]),
        { provide: DriverService, useValue: driverServiceMock },
        { provide: DialogService, useValue: dialogServiceMock },
      ],
    }).compileComponents();

    router = TestBed.inject(Router);
    spyOn(router, 'navigate');

    fixture = TestBed.createComponent(RegisterDriver);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create component', () => {
    expect(component).toBeTruthy();
  });

  it('should call service and open success dialog', () => {
    component.currentStep = 1;

    component.driverForm = {
      onSubmit: () => {},
      isFormValid: () => true,
    } as any;

    component.vehicleForm = {
      onSubmit: () => {},
      isFormValid: () => true,
    } as any;

    component.driverData = {
      email: 'a@a.com',
      password: '123',
      name: 'Marko',
      surname: 'Markovic',
      phoneNumber: '123',
      address: 'Adresa',
    };

    component.vehicleData = {
      make: 'BMW',
      model: 'X5',
      licensePlate: 'NS-1234-AA',
      color: 'Black',
      vehicleType: 'Standard',
      seats: 4,
      petFriendly: false,
      babyFriendly: false,
    };

    component.finishRegistration();

    expect(driverServiceMock.registerDriver).toHaveBeenCalled();
    expect(dialogServiceMock.open).toHaveBeenCalledWith(
      'Registration Successful',
      jasmine.any(String),
      false,
    );
  });

  it('should open error dialog on service error', () => {
    driverServiceMock.registerDriver.and.returnValue(
      throwError(() => ({
        error: { message: 'Error occurred' },
      })),
    );

    component.currentStep = 1;

    component.driverForm = { onSubmit: () => {}, isFormValid: () => true } as any;
    component.vehicleForm = { onSubmit: () => {}, isFormValid: () => true } as any;

    component.finishRegistration();

    expect(dialogServiceMock.open).toHaveBeenCalledWith(
      'Registration Failed',
      'Error occurred',
      true,
    );
  });
});
