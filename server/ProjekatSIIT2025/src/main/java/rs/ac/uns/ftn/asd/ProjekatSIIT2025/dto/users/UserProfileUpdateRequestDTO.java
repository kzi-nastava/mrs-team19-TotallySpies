package rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.users;

import jakarta.validation.constraints.Pattern;

public class UserProfileUpdateRequestDTO {
    private String name;
    private String lastName;
    @Pattern(regexp = "^\\+381 \\d{2} \\d{3} (\\d{3}|\\d{4})$",
    message = "Phone number must be in format +381 ** *** ***(*)")
    private String phoneNumber;
    private String address;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
