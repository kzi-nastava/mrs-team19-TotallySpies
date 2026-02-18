import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { Chart, registerables } from 'chart.js';
import { ReportResponseDTO } from '../../shared/models/report.model';
import { UserService } from '../../shared/services/user.service';
import { AuthService } from '../../core/auth/services/auth.service';
import { CommonModule } from '@angular/common'; 
import { FormsModule } from '@angular/forms'; 

Chart.register(...registerables);

@Component({
  selector: 'app-report',
  imports: [CommonModule, FormsModule],
  templateUrl: './report.component.html',
  styleUrl: './report.component.css',
})
export class ReportComponent implements OnInit {
  filterStart: string = '';
  filterEnd: string = '';
  targetEmail: string = '';
  userRole: string = '';
  reportData?: ReportResponseDTO;

  charts: Chart[] = [];

  constructor(
    private userService: UserService, 
    private authService: AuthService,
    private cdr: ChangeDetectorRef) 
    { }

  ngOnInit(): void {
    this.userRole = this.authService.getRole();
    const today = new Date();
    const monthAgo = new Date();
    monthAgo.setDate(today.getDate() - 30);

    this.filterEnd = today.toISOString().split('T')[0];
    this.filterStart = monthAgo.toISOString().split('T')[0];
  }

  generateReport(): void {
    this.userService.getReport(this.filterStart, this.filterEnd, this.targetEmail).subscribe({
      next: (data) => {
        this.reportData = data;

        this.cdr.detectChanges();

        this.updateCharts();
      }
    });
  }

  updateCharts() {
    // unisti stare grafikone ako postoje
    this.charts.forEach(c => c.destroy());
    this.charts = [];

    const labels = this.reportData!.dailyData.map(d => d.date);

    this.charts.push(this.createChart('moneyChart', labels, this.reportData!.dailyData.map(d => d.money), 'Money (RSD)', '#6E58C6'));
    this.charts.push(this.createChart('countChart', labels, this.reportData!.dailyData.map(d => d.count), 'Number of Rides', '#FF6384'));
    this.charts.push(this.createChart('distanceChart', labels, this.reportData!.dailyData.map(d => d.distance), 'Distance (km)', '#36A2EB'));
  }

  createChart(id: string, labels: string[], data: number[], label: string, color: string) {
    return new Chart(id, {
      type: 'line',
      data: {
        labels: labels,
        datasets: [{
          label: label,
          data: data,
          borderColor: color,
          backgroundColor: color + '33', // 33 je providnost
          fill: true,
          tension: 0.4
        }]
      },
      options: { responsive: true, plugins: { legend: { labels: { color: '#fff' } } } }
    });
  }
}