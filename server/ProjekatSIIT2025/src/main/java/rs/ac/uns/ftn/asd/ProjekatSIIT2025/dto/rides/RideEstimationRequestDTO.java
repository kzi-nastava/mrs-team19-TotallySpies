package rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.rides;

public class RideEstimationRequestDTO {
    private String startingAddress;
    private String destinationAddress;

    public String getStartingAddress() {
        return startingAddress;
    }

    public void setStartingAddress(String startingAddress) {
        this.startingAddress = startingAddress;
    }

    public void setDestinationAddress(String destinationAddress) {
        this.destinationAddress = destinationAddress;
    }

    public String getDestinationAddress() {
        return destinationAddress;
    }
}
