package rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.users;

public class UserProfileResponseDTO {
    private String name;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String address;
    private String profilePicture;

    public UserProfileResponseDTO(String name, String lastName, String profilePicture) {
        this.name = name;
        this.lastName = lastName;
        this.profilePicture = profilePicture;
    }

    public UserProfileResponseDTO() {
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
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
}
