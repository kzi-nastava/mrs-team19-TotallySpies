export interface DailyReportDTO {
  date: string; // LocalDate sa backenda dolazi kao string
  count: number;
  distance: number;
  money: number;
}

export interface ReportResponseDTO {
  dailyData: DailyReportDTO[];
  totalSum: number;
  averageMoney: number;
  totalDistance: number;
  totalCount: number;
}