import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import {
  ActivatedRoute,
  Router,
  convertToParamMap,
  provideRouter,
} from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { of, throwError } from 'rxjs';

import { RegisterComponent } from './register.component';
import { AuthService } from '../../services/auth.service';

describe('RegisterComponent', () => {
  let component: RegisterComponent;
  let fixture: ComponentFixture<RegisterComponent>;

  let authServiceSpy: jasmine.SpyObj<AuthService>;
  let routerSpy: jasmine.SpyObj<Router>;
  let snackBarSpy: jasmine.SpyObj<MatSnackBar>;

  beforeEach(async () => {
    authServiceSpy = jasmine.createSpyObj<AuthService>('AuthService', ['register']);
    routerSpy = jasmine.createSpyObj<Router>('Router', ['navigate']);
    snackBarSpy = jasmine.createSpyObj<MatSnackBar>('MatSnackBar', ['open']);

    await TestBed.configureTestingModule({
      imports: [RegisterComponent],
      providers: [
        provideRouter([]),

        // ubaci spy-eve u DI
        { provide: AuthService, useValue: authServiceSpy },
        { provide: Router, useValue: routerSpy },
        { provide: MatSnackBar, useValue: snackBarSpy },

        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: { paramMap: convertToParamMap({ id: '1' }) },
            paramMap: of(convertToParamMap({ id: '1' })),
            queryParamMap: of(convertToParamMap({})),
            params: of({ id: '1' }),
            queryParams: of({}),
          },
        },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(RegisterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  function fillValidForm() {
    component.registerForm.setValue({
      email: 'test@example.com',
      password: 'password1',
      confirmedPassword: 'password1',
      name: 'Ana',
      lastName: 'Anić',
      address: 'Ulica 1',
      phoneNumber: '12345678901',
    });
  }

  function submitForm() {
    const formDe = fixture.debugElement.query(By.css('form'));
    expect(formDe).toBeTruthy();
    formDe.triggerEventHandler('submit', {});
    fixture.detectChanges();
  }

  it('should create Register component', () => {
    expect(component).toBeTruthy();
  });

  // it('should have disabled Register button when form is invalid', () => {
  //   component.registerForm.reset();
  //   fixture.detectChanges();

  //   const btn = fixture.debugElement.query(By.css('button[type="submit"]'));
  //   expect(btn).toBeTruthy();
  //   expect(btn.nativeElement.disabled).toBeTrue();
  // });

  // it('should enable Register button when form is valid', () => {
  //   fillValidForm();
  //   fixture.detectChanges();

  //   const btn = fixture.debugElement.query(By.css('button[type="submit"]'));
  //   expect(btn).toBeTruthy();
  //   expect(btn.nativeElement.disabled).toBeFalse();
  // });

  it('should markAllAsTouched and NOT call service when form is invalid', () => {
    component.registerForm.reset();
    const markSpy = spyOn(component.registerForm, 'markAllAsTouched').and.callThrough();

    submitForm();

    expect(markSpy).toHaveBeenCalled();
    expect(authServiceSpy.register).not.toHaveBeenCalled();
  });

  it('should send FormData with all fields and call AuthService.register on valid submit', () => {
    fillValidForm();
    fixture.detectChanges();

    const appendSpy = spyOn(FormData.prototype as any, 'append').and.callThrough();
    authServiceSpy.register.and.returnValue(of('User registered!'));

    submitForm();

    expect(authServiceSpy.register).toHaveBeenCalledTimes(1);

    expect(appendSpy).toHaveBeenCalledWith('email', 'test@example.com');
    expect(appendSpy).toHaveBeenCalledWith('password', 'password1');
    expect(appendSpy).toHaveBeenCalledWith('confirmedPassword', 'password1');
    expect(appendSpy).toHaveBeenCalledWith('name', 'Ana');
    expect(appendSpy).toHaveBeenCalledWith('lastName', 'Anić');
    expect(appendSpy).toHaveBeenCalledWith('address', 'Ulica 1');
    expect(appendSpy).toHaveBeenCalledWith('phoneNumber', '12345678901');
    expect(appendSpy).toHaveBeenCalledWith('client', 'web');
  });

  it('should append profilePicture when selectedFile exists', () => {
    fillValidForm();
    fixture.detectChanges();

    const appendSpy = spyOn(FormData.prototype as any, 'append').and.callThrough();

    const file = new File(['abc'], 'avatar.png', { type: 'image/png' });
    component.selectedFile = file;

    authServiceSpy.register.and.returnValue(of('ok'));

    submitForm();

    expect(authServiceSpy.register).toHaveBeenCalledTimes(1);
    expect(appendSpy).toHaveBeenCalledWith('profilePicture', file);
  });

  // it('should show success snackbar and navigate to login on successful registration', fakeAsync(() => {
  //   fillValidForm();
  //   fixture.detectChanges();

  //   authServiceSpy.register.and.returnValue(of('User registered!'));

  //   submitForm();
  //   tick(); // ako ima async nakon subscribe-a

  //   expect(snackBarSpy.open).toHaveBeenCalledWith(
  //     'Registration successful! Check your email.',
  //     'OK',
  //     jasmine.objectContaining({
  //       duration: 2000,
  //       horizontalPosition: 'center',
  //       verticalPosition: 'top',
  //       panelClass: ['confirm-snackbar'],
  //     })
  //   );

  //   expect(routerSpy.navigate).toHaveBeenCalledWith(['login']);
  // }));

  it('should show success snackbar and navigate to login on successful registration', () => {
    fillValidForm();
    fixture.detectChanges();

    authServiceSpy.register.and.returnValue(of('User registered!'));

    submitForm();

    expect(snackBarSpy.open).toHaveBeenCalledWith(
      'Registration successful! Check your email.',
      'OK',
      jasmine.objectContaining({
        duration: 2000,
        horizontalPosition: 'center',
        verticalPosition: 'top',
        panelClass: ['confirm-snackbar'],
      })
    );

    expect(routerSpy.navigate).toHaveBeenCalledWith(['login']);
  });
  it('should show "Email already in use" when backend returns 409', () => {
    fillValidForm();
    fixture.detectChanges();

    authServiceSpy.register.and.returnValue(throwError(() => ({ status: 409 })));

    submitForm();

    expect(snackBarSpy.open).toHaveBeenCalledWith(
      'Email already in use',
      'OK',
      jasmine.objectContaining({
        horizontalPosition: 'center',
        verticalPosition: 'top',
        panelClass: ['error-snackbar'],
      })
    );
  });

  it('should show "Wrong client specified!" when backend returns 404', () => {
    fillValidForm();
    fixture.detectChanges();

    authServiceSpy.register.and.returnValue(throwError(() => ({ status: 404 })));

    submitForm();

    expect(snackBarSpy.open).toHaveBeenCalledWith(
      'Wrong client specified!',
      'OK',
      jasmine.any(Object)
    );
  });

  it('should show "Password and confirmed password need to match!" when backend returns 400', () => {
    fillValidForm();
    fixture.detectChanges();

    authServiceSpy.register.and.returnValue(throwError(() => ({ status: 400 })));

    submitForm();

    expect(snackBarSpy.open).toHaveBeenCalledWith(
      'Password and confirmed password need to match!',
      'OK',
      jasmine.any(Object)
    );
  });

  it('should show validation error for email when touched and invalid', () => {
    component.email.setValue('');
    component.email.markAsTouched();
    fixture.detectChanges();

    const errorEls = fixture.debugElement.queryAll(By.css('.error, mat-error'));
    const errorText = errorEls
      .map(e => (e.nativeElement.textContent as string).trim())
      .join(' | ');

    expect(errorText.toLowerCase()).toContain('email');
  });
});
