import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-admin-pricing',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-pricing.component.html',
  styleUrl: './admin-pricing.component.css',
})
export class AdminPricingComponent implements OnInit {
  prices: any[] = []; 
  constructor(private http: HttpClient, private cdr: ChangeDetectorRef) {}

  ngOnInit() {
    this.fetchPrices();
  }

  fetchPrices() {
  this.http.get<any[]>('http://localhost:8080/api/v1/prices').subscribe(data => {
    this.prices = data;
    this.cdr.detectChanges();
  });
}

  updatePrice(priceObj: any) {
    this.http.put('http://localhost:8080/api/v1/prices', {
      vehicleType: priceObj.vehicleType,
      newPrice: priceObj.basePrice
    }).subscribe({
      next: () => alert(`Price for ${priceObj.vehicleType} updated!`),
      error: () => alert('Error while updating price!')
    });
  }
}