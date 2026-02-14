import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Router } from '@angular/router';

export type DriverSearchStatus = 'IDLE' | 'SEARCHING' | 'FOUND' | 'NOT_FOUND' | 'BLOCKED'  | 'SCHEDULED';

@Component({
  selector: 'app-driver-search-modal',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './driver-search-modal.component.html',
  styleUrl: './driver-search-modal.component.css',
})
export class DriverSearchModalComponent {
  @Input() status: DriverSearchStatus = 'IDLE';
  @Input() errorMessage: string | null = null;
  @Output() close = new EventEmitter<void>();

  closeModal() {
  console.log('Modal: Emitujem close event...');
  this.close.emit();
}
}