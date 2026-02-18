import { ComponentFixture, TestBed } from '@angular/core/testing';

import { VehicleForm } from './vehicle-form';

describe('VehicleForm', () => {
  let component: VehicleForm;
  let fixture: ComponentFixture<VehicleForm>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [VehicleForm]
    }).compileComponents();

    fixture = TestBed.createComponent(VehicleForm);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be invalid when empty', () => {
    expect(component.isFormValid()).toBeFalse();
  });

  it('should validate correct license plate', () => {
    component.make.set('BMW');
    component.model.set('X5');
    component.licensePlate.set('NS-1234-AA');
    component.seats.set(4);
    component.color.set('Black');

    expect(component.isFormValid()).toBeTrue();
  });

  it('should be invalid with incorrect license plate', () => {
    component.make.set('BMW');
    component.model.set('X5');
    component.seats.set(4);
    component.color.set('Black');
    
    component.licensePlate.set('NS-1234');
    expect(component.isFormValid()).toBeFalse();

    component.licensePlate.set('1234-NS-AA');
    expect(component.isFormValid()).toBeFalse();

    component.licensePlate.set('NS-1234-AAA');
    expect(component.isFormValid()).toBeFalse();

    component.licensePlate.set('Ns-123-AA');
    expect(component.isFormValid()).toBeFalse();
  });

});