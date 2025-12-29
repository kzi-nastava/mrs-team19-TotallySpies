package rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.rides;

public class RideEstimationResponseDTO {
    private double estimatedTime;
    private double estimatedMoney;

    public double getEstimatedTime() {
        return estimatedTime;
    }

    public void setEstimatedTime(double estimatedTime) {
        this.estimatedTime = estimatedTime;
    }

    public double getEstimatedMoney() {
        return estimatedMoney;
    }

    public void setEstimatedMoney(double estimatedMoney) {
        this.estimatedMoney = estimatedMoney;
    }
}
