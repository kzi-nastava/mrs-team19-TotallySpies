package rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.rides;

import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.RideStatus;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.VehicleType;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class RideResponseDTO {
    private Long id;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private double totalPrice;
    private ArrayList<Long> paths;
    private RideStatus status;

    public RideResponseDTO() {}

    public RideResponseDTO(Long id, LocalDateTime startTime, LocalDateTime endTime, double totalPrice, ArrayList<Long> paths, RideStatus status) {
        this.id = id;
        this.startTime = startTime;
        this.endTime = endTime;
        this.totalPrice = totalPrice;
        this.paths = paths;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public ArrayList<Long> getPaths() {
        return paths;
    }

    public void setPaths(ArrayList<Long> paths) {
        this.paths = paths;
    }



    public RideStatus getStatus() {
        return status;
    }

    public void setStatus(RideStatus status) {
        this.status = status;
    }

}
