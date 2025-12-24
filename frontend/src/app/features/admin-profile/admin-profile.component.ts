import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms'; 

@Component({
  selector: 'app-admin-profile',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-profile.component.html',
  styleUrl: './admin-profile.component.css',
})
export class AdminProfileComponent {
  editingField: string | null = null;
  showPassword: boolean = false;
  
  currentUser = {
    name: 'Elena',
    surname: 'Vukovic',
    email: 'elenavukovic@gmail.com',
    phone: '+381 123 1234',
    address: 'Narodnog fronta 24, Novi Sad'
  };

  toggleEdit(field: string): void {
    if (this.editingField === field) {
      this.editingField = null;
    } else {
      this.editingField = field;
    }
  }
  activeTab: string = 'drivers'; 

  setActiveTab(tabName: string): void {
    this.activeTab = tabName;
  }

  driversList = [
    { email: 'janecooper@gmail.com', name: 'Jane Cooper', status: 'Active', id: 1 },
    { email: 'wadewarren@gmail.com', name: 'Wade Warren', status: 'Active', id: 2 },
    { email: 'estherhoward@gmail.com', name: 'Esther Howard', status: 'Inactive', id: 3 }, 
    { email: 'cameronw@gmail.com', name: 'Cameron Williamson', status: 'Active', id: 4 },
    { email: 'brooklyns@gmail.com', name: 'Brooklyn Simmons', status: 'Inactive', id: 5 },
    { email: 'lesliealex@gmail.com', name: 'Leslie Alexander', status: 'Inactive', id: 6 },
    { email: 'jennywilson@gmail.com', name: 'Jenny Wilson', status: 'Active', id: 7 },
    { email: 'guyhawkins@gmail.com', name: 'Guy Hawkins', status: 'Active', id: 8 },
  ];

  usersList = [
    { email: 'janecooper@gmail.com', name: 'Jane Cooper', id: 1 },
    { email: 'wadewarren@gmail.com', name: 'Wade Warren', id: 2 },
    { email: 'estherhoward@gmail.com', name: 'Esther Howard', id: 3 },
    { email: 'cameronw@gmail.com', name: 'Cameron Williamson', id: 4 },
    { email: 'brooklyns@gmail.com', name: 'Brooklyn Simmons', id: 5 },
  ];

  getStatusClass(status: string): string {
    return status === 'Active' ? 'status-active' : 'status-inactive';
  }
}
