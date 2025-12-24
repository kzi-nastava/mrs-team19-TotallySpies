import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms'; 

interface UserProfile {
  name: string;
  surname: string;
  email: string;
  phone: string;
  address: string;
}

@Component({
  selector: 'app-driver-profile',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './driver-profile.component.html',
  styleUrls: ['./driver-profile.component.css'],
})
export class DriverProfileComponent{
  editingField: string | null = null;
  showPassword: boolean = false;

  originalUser: UserProfile = {
    name: 'Elena',
    surname: 'Vukovic',
    email: 'elenavukovic@gmail.com',
    phone: '+381 123 1234',
    address: 'Narodnog fronta 24, Novi Sad'
  };
  currentUser: UserProfile = { ...this.originalUser };

  driverNotes = [
    { text: 'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Maecenas nec nibh eget magna congue viverra.' },
    { text: 'Vivamus ut lectus in dolor elementum interdum. Sed malesuada ornare molestie.' },
    { text: 'Suspendisse potenti. Aenean urna purus, feugiat at accumsan in, condimentum non massa.' }
  ];

  enableEdit(field: string): void {
    if (this.editingField === field) {
    this.editingField = null;
    } else {
      this.editingField = field;
    }
  }

  isFieldChanged(field: keyof UserProfile): boolean {
    return this.currentUser[field] !== this.originalUser[field];
  }

  get hasAnyChanges(): boolean {
    return JSON.stringify(this.currentUser) !== JSON.stringify(this.originalUser);
  }

  saveChanges(): void {
    if (this.hasAnyChanges) {
      console.log('Saving data...', this.currentUser);
      this.originalUser = { ...this.currentUser };
      this.editingField = null;
    }
  }
}