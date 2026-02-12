package rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.reports;

import java.util.List;

public class ReportResponseDTO {
    private List<DailyReportDTO> dailyData;
    private double totalSum;      // ukupno zaradjeno/potroseno
    private double averageMoney;  // prodjek po danu
    private double totalDistance; // ukupna kilometraza
    private long totalCount;

    public ReportResponseDTO(){

    }

    public ReportResponseDTO(List<DailyReportDTO> dailyData, double totalSum, double averageMoney, double totalDistance, long totalCount) {
        this.dailyData = dailyData;
        this.totalSum = totalSum;
        this.averageMoney = averageMoney;
        this.totalDistance = totalDistance;
        this.totalCount = totalCount;
    }

    public List<DailyReportDTO> getDailyData() {
        return dailyData;
    }

    public void setDailyData(List<DailyReportDTO> dailyData) {
        this.dailyData = dailyData;
    }

    public double getTotalSum() {
        return totalSum;
    }

    public void setTotalSum(double totalSum) {
        this.totalSum = totalSum;
    }

    public double getAverageMoney() {
        return averageMoney;
    }

    public void setAverageMoney(double averageMoney) {
        this.averageMoney = averageMoney;
    }

    public double getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(double totalDistance) {
        this.totalDistance = totalDistance;
    }

    public long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }
}
