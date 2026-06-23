import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { DriverRegistrationComponent } from './driver-registration.component';
import { AdminService } from '../../shared/services/admin.service';
import { Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { of, throwError } from 'rxjs';
import { VehicleType } from '../../shared/models/users/user.model';

describe('DriverRegistrationComponent (Angular 21 Standalone)', () => {

  let component: DriverRegistrationComponent;
  let fixture: ComponentFixture<DriverRegistrationComponent>;

  let adminServiceSpy: jasmine.SpyObj<AdminService>;
  let routerSpy: jasmine.SpyObj<Router>;
  let snackBarSpy: jasmine.SpyObj<MatSnackBar>;

  beforeEach(async () => {

    adminServiceSpy = jasmine.createSpyObj('AdminService', ['createDriver']);
    routerSpy = jasmine.createSpyObj('Router', ['navigate']);
    snackBarSpy = jasmine.createSpyObj('MatSnackBar', ['open']);

    const snackBarRefMock = {
      afterDismissed: () => of(void 0)
    };

    snackBarSpy.open.and.returnValue(snackBarRefMock as any);

    await TestBed.configureTestingModule({
      imports: [DriverRegistrationComponent],
      providers: [
        { provide: AdminService, useValue: adminServiceSpy },
        { provide: Router, useValue: routerSpy },
        { provide: MatSnackBar, useValue: snackBarSpy }
      ]
    })
    .overrideComponent(DriverRegistrationComponent, {
      set: {
        providers: [
          { provide: AdminService, useValue: adminServiceSpy },
          { provide: Router, useValue: routerSpy },
          { provide: MatSnackBar, useValue: snackBarSpy }
        ]
      }
    })
    .compileComponents();

    fixture = TestBed.createComponent(DriverRegistrationComponent);
    component = fixture.componentInstance;

    spyOn(window, 'alert');
  });

  // helper metode
  function fillDriverOnly() {
    component.driverForm.patchValue({
      email: 'john.doe@gmail.com',
      firstName: 'John',
      lastName: 'Doe',
      address: 'Bulevar 1',
      phone: '06412345678'
    });
  }

  function fillVehicle() {
    component.driverForm.patchValue({
      model: 'Audi A4',
      type: VehicleType.STANDARD,
      licensePlate: 'NS-123-AA',
      seats: 4,
      babyFriendly: true,
      petFriendly: false
    });
  }

  it('should create component', () => {
    fixture.detectChanges();
    expect(component).toBeTruthy();
  });

  it('should initialize form with default values', () => {
    fixture.detectChanges();
    expect(component.driverForm.get('email')?.value).toBe('');
    expect(component.driverForm.get('firstName')?.value).toBe('');
    expect(component.driverForm.get('lastName')?.value).toBe('');
    expect(component.driverForm.get('address')?.value).toBe('');
    expect(component.driverForm.get('phone')?.value).toBe('');

    expect(component.driverForm.get('seats')?.value).toBe(4);
    expect(component.driverForm.get('babyFriendly')?.value).toBeFalse();
    expect(component.driverForm.get('petFriendly')?.value).toBeFalse();
  });


  it('should be invalid when required fields are empty', () => {
    fixture.detectChanges();
    expect(component.driverForm.valid).toBeFalse();

    expect(component.driverForm.get('email')?.hasError('required')).toBeTrue();
    expect(component.driverForm.get('firstName')?.hasError('required')).toBeTrue();
    expect(component.driverForm.get('lastName')?.hasError('required')).toBeTrue();
    expect(component.driverForm.get('address')?.hasError('required')).toBeTrue();
    expect(component.driverForm.get('phone')?.hasError('required')).toBeTrue();
  });

  it('should validate email format', () => {
    fixture.detectChanges();
    component.driverForm.get('email')?.setValue('invalid-email');

    expect(component.driverForm.get('email')?.valid).toBeFalse();
    expect(component.driverForm.get('email')?.hasError('email')).toBeTrue();
  });

  it('should validate phone pattern', () => {
    fixture.detectChanges();
    component.driverForm.get('phone')?.setValue('12345');

    expect(component.driverForm.get('phone')?.valid).toBeFalse();
    expect(component.driverForm.get('phone')?.hasError('pattern')).toBeTrue();
  });

  it('should validate seats min value', () => {
    fixture.detectChanges();
    component.driverForm.get('seats')?.setValue(0);

    expect(component.driverForm.get('seats')?.valid).toBeFalse();
    expect(component.driverForm.get('seats')?.hasError('min')).toBeTrue();
  });

  it('should keep driver data and remain invalid without vehicle info', () => {
    fixture.detectChanges();
    fillDriverOnly();

    expect(component.driverForm.valid).toBeFalse();

    component.addDriver();

    expect(adminServiceSpy.createDriver).not.toHaveBeenCalled();

    expect(component.driverForm.get('email')?.value).toBe('john.doe@gmail.com');
    expect(component.driverForm.get('firstName')?.value).toBe('John');
    expect(component.driverForm.get('lastName')?.value).toBe('Doe');
  });

  it('should NOT call service when form is invalid', () => {
    fixture.detectChanges();
    fillDriverOnly();

    component.addDriver();

    expect(adminServiceSpy.createDriver).not.toHaveBeenCalled();
    expect(snackBarSpy.open).not.toHaveBeenCalled();
  });

  it('should send correct DTO when form is valid', () => {
    fixture.detectChanges();

    adminServiceSpy.createDriver.and.returnValue(of(void 0));

    fillDriverOnly();
    fillVehicle();
    component.addDriver();

    expect(adminServiceSpy.createDriver).toHaveBeenCalledOnceWith({
      email: 'john.doe@gmail.com',
      firstName: 'John',
      lastName: 'Doe',
      address: 'Bulevar 1',
      phone: '06412345678',
      model: 'Audi A4',
      type: VehicleType.STANDARD,
      licensePlate: 'NS-123-AA',
      seats: 4,
      babyFriendly: true,
      petFriendly: false
    });
  });

  it('should show success snackbar and navigate on success', async () => {
    adminServiceSpy.createDriver.and.returnValue(of(void 0));

    fixture.detectChanges();

    fillDriverOnly();
    fillVehicle();

    component.driverForm.updateValueAndValidity();
    fixture.detectChanges();

    await component.addDriver();
    await fixture.whenStable();

    expect(snackBarSpy.open).toHaveBeenCalledWith(
      'Driver successfully created!', 'OK', jasmine.any(Object)
    );

    expect(routerSpy.navigate).toHaveBeenCalledWith(['/profile']);
  });

  it('should show 409 conflict error message', async () => {
    adminServiceSpy.createDriver.and.returnValue(
      throwError(() => ({ status: 409 }))
    );

    fixture.detectChanges();

    fillDriverOnly();
    fillVehicle();

    component.driverForm.updateValueAndValidity();
    fixture.detectChanges();

    await component.addDriver();
    await fixture.whenStable();

    expect(snackBarSpy.open).toHaveBeenCalledWith(
      'Driver with this email already exists.', 'OK', jasmine.any(Object)
    );
  });

  it('should show 400 validation error messages', async () => {
    adminServiceSpy.createDriver.and.returnValue(
      throwError(() => ({
        status: 400,
        error: { errors: { email: 'Invalid email', phone: 'Invalid phone' } }
      }))
    )

    fixture.detectChanges();

    fillDriverOnly();
    fillVehicle();

    component.driverForm.updateValueAndValidity();
    fixture.detectChanges();

    await component.addDriver();
    await fixture.whenStable();

    expect(snackBarSpy.open).toHaveBeenCalledWith(
      'Invalid email, Invalid phone', 'OK', jasmine.any(Object)
    );
  });

});
