package com.ftn.mobile.data.remote.dto;

import java.util.List;

public class ReportResponseDTO {
    private List<DailyReportDTO> dailyData;
    private double totalSum;
    private double averageMoney;
    private double totalDistance;
    private long totalCount;

    public ReportResponseDTO() {}

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
