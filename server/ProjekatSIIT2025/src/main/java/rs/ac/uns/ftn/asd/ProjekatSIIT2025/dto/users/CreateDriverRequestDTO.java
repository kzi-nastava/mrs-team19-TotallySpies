package rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.users;

import jakarta.validation.constraints.*;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.VehicleType;

public class CreateDriverRequestDTO {
    // driver
    @NotBlank(message = "Email is required")
    @Email(message = "Email format is invalid")
    private String email;

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotBlank(message = "Phone number is required")
    @Pattern(
            regexp = "^(\\+381 \\d{2} \\d{3} \\d{3,4}|\\d{11})$",
            message = "Phone number must be in format +381 ** *** ***(*) or have 11 digits"
    )
    private String phone;

    private String address;

    // vehicle
    @NotBlank(message = "Model is required")
    private String model;

    @NotNull(message = "Vehicle type is required")
    private VehicleType type;

    @NotBlank(message = "License plate is required")
    private String licensePlate;

    @Min(value = 1, message = "Vehicle must have at least 1 seat")
    @Max(value = 8, message = "Vehicle cannot have more than 8 seats")
    private int seats;
    private boolean babyFriendly;
    private boolean petFriendly;

    public CreateDriverRequestDTO() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public VehicleType getType() {
        return type;
    }

    public void setType(VehicleType type) {
        this.type = type;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public int getSeats() {
        return seats;
    }

    public void setSeats(int seats) {
        this.seats = seats;
    }

    public boolean isBabyFriendly() {
        return babyFriendly;
    }

    public void setBabyFriendly(boolean babyFriendly) {
        this.babyFriendly = babyFriendly;
    }

    public boolean isPetFriendly() {
        return petFriendly;
    }

    public void setPetFriendly(boolean petFriendly) {
        this.petFriendly = petFriendly;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
