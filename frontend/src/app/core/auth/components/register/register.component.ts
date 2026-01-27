import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule],
  templateUrl: './register.component.html',
  styleUrl: './register.component.css',
})
export class RegisterComponent {
  constructor(private authService: AuthService,private router: Router,private snackBar: MatSnackBar) {}

  registerForm = new FormGroup({
    email: new FormControl('', [Validators.required, Validators.email]),
    password: new FormControl('', [Validators.required,Validators.minLength(8)]),
    confirmedPassword: new FormControl('', [Validators.required,Validators.minLength(8)]),
    name: new FormControl('', Validators.required),
    lastName: new FormControl('', Validators.required),
    address: new FormControl('', Validators.required),
    phoneNumber: new FormControl('', [Validators.required,Validators.pattern(/^\d{11}$/)])
  });

  get email() {
    return this.registerForm.controls.email;
  }
  get password() {
    return this.registerForm.controls.password;
  }
  get confirmedPassword() {
    return this.registerForm.controls.confirmedPassword;
  }
  get name() {
    return this.registerForm.controls.name;
  }
  get lastName() {
    return this.registerForm.controls.lastName;
  }
  get address() {
    return this.registerForm.controls.address;
  }
  get phoneNumber() {
    return this.registerForm.controls.phoneNumber;
  }

  selectedFile: File | null = null;
  selectedFileName = '';
  previewUrl: string | null = null;

  onFileSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    if (!input.files || input.files.length === 0) return;
    this.setSelectedFile(input.files[0]);
    // allow picking same file again later
    input.value = '';
  }

  onDragOver(event: DragEvent) {
    event.preventDefault(); // needed to allow drop
  }

  onDrop(event: DragEvent) {
    event.preventDefault();
    const file = event.dataTransfer?.files?.[0];
    if (!file) return;
    this.setSelectedFile(file);
  }

  private setSelectedFile(file: File) {
    if (!file.type.startsWith('image/')) {
      this.snackBar.open('Please select an image file.', 'OK', {
        duration: 1500,
        horizontalPosition: 'center',
        verticalPosition: 'top',
      });
      return;
    }

    const maxSize = 5 * 1024 * 1024; // 5MB
    if (file.size > maxSize) {
      this.snackBar.open('Image is too large (max 5MB).', 'OK', {
        duration: 1500,
        horizontalPosition: 'center',
        verticalPosition: 'top',
      });
      return;
    }

    this.selectedFile = file;
    this.selectedFileName = file.name;

    const reader = new FileReader();
    reader.onload = () => (this.previewUrl = reader.result as string);
    reader.readAsDataURL(file);
  }
  private extractErrorMessage(err: any): string {
    return (
      err?.error?.message ??
      (typeof err?.error === 'string' ? err.error : null) ??
      (err?.status === 409 ? 'Email already in use' : null) ??
      (err?.status === 400 ? 'Password and confirmed password need to match!' : null) ??
      'Registration failed, image is too big.'
    );
  }
  register(): void {
    if (this.registerForm.invalid) {
      this.registerForm.markAllAsTouched();
      return;
    }
    const formData = new FormData();
    formData.append('email', this.email.value ?? '');
    formData.append('password', this.password.value ?? '');
    formData.append('confirmedPassword', this.confirmedPassword.value ?? '');
    formData.append('name', this.name.value ?? '');
    formData.append('lastName', this.lastName.value ?? '');
    formData.append('address', this.address.value ?? '');
    formData.append('phoneNumber', this.phoneNumber.value ?? '');

    // attach file if selected
    if (this.selectedFile) {
      formData.append('profilePicture', this.selectedFile);
    }

    this.authService.register(formData).subscribe({
      next: (response) => {
        console.log('SUCCESS', response);
        this.snackBar.open('Registration successful! Check your email.', 'OK', {
          duration: 2000,
          horizontalPosition: 'center',
          verticalPosition: 'top',
          panelClass : ['confirm-snackbar']
        });
        this.router.navigate(['login']);
      },
      error: (err) => {
        this.snackBar.open(this.extractErrorMessage(err), 'OK', {
            horizontalPosition : 'center',
            verticalPosition : 'top',
            panelClass : ['error-snackbar']
          })
        },
    });
  }
}
