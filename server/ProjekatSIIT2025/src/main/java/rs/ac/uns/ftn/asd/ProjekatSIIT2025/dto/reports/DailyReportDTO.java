package rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.reports;

import java.time.LocalDate;

public class DailyReportDTO {
    private LocalDate date; // Može i LocalDate
    private long count;
    private double distance;
    private double money;

    public DailyReportDTO(){

    }

    public DailyReportDTO(LocalDate date, long count, double distance, double money) {
        this.date = date;
        this.count = count;
        this.distance = distance;
        this.money = money;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }
}
