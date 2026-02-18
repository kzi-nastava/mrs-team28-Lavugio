import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DriverForm } from './driver-form';

describe('DriverForm', () => {
  let component: DriverForm;
  let fixture: ComponentFixture<DriverForm>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DriverForm],
    }).compileComponents();

    fixture = TestBed.createComponent(DriverForm);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be invalid when empty', () => {
    expect(component.isFormValid()).toBeFalse();
  });

  it('should validate correct email', () => {
    component.email.set('test@test.com');
    component.name.set('Marko');
    component.surname.set('Markovic');
    component.address.set('Adresa 1');
    component.phoneNumber.set('123456');

    expect(component.isFormValid()).toBeTrue();
  });

  it('should emit data when signals change', () => {
    spyOn(component.dataChange, 'emit');

    component.email.set('a@a.com');
    fixture.detectChanges();

    expect(component.dataChange.emit).toHaveBeenCalled();
  });

  it('should invalidate incorrect email format', () => {
    component.email.set('invalid-email');
    component.name.set('Marko');
    component.surname.set('Markovic');
    component.address.set('Adresa 1');
    component.phoneNumber.set('123456');

    expect(component.isFormValid()).toBeFalse();
  });

  it('should set submitted to true on submit', () => {
    expect(component.submitted()).toBeFalse();
    
    component.onSubmit();
    
    expect(component.submitted()).toBeTrue();
  });
});
