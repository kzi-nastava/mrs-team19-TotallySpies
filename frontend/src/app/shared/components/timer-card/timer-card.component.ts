import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import {MatProgressSpinnerModule} from '@angular/material/progress-spinner';

@Component({
  selector: 'app-timer-card',
  imports: [CommonModule, MatProgressSpinnerModule],
  templateUrl: './timer-card.component.html',
  styleUrl: './timer-card.component.css',
  standalone: true,
})
export class TimerCardComponent {
  @Input() progressValue: number = 70;
  @Input() minutes: number = 7;
}
