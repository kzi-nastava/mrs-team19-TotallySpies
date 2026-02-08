package rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

public class UserRegisterRequestDTO {
    @NotBlank
    @Email(message = "Email format is not valid")
    private String email;
    @NotBlank
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;
    @NotBlank
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String confirmedPassword;
    @NotBlank(message = "Name is required")
    private String name;
    @NotBlank(message = "Last name is required")
    private String lastName;
    private MultipartFile profilePicture;
    @NotBlank
    @Pattern(
            regexp = "^\\d{11}$",
            message = "Phone number must have exactly 11 digits"
    )
    @NotBlank(message = "Phone number is required")
    private String phoneNumber;
    @NotBlank(message = "Address is required")
    private String address;
    @NotBlank(message = "Client is required (mobile or web)")
    private String client;

    public UserRegisterRequestDTO(){}

    public String getEmail(){
        return this.email;
    }
    public void setEmail(String email){
        this.email = email;
    }
    public String getPassword(){
        return this.password;
    }
    public void setPassword(String password){
        this.password = password;
    }
    public String getName(){
        return this.name;
    }
    public void setName(String name){
        this.name = name;
    }
    public String getLastName(){
        return this.lastName;
    }
    public void setLastName(String lastName){
        this.lastName = lastName;
    }

    public MultipartFile getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(MultipartFile profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getPhoneNumber(){
        return this.phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber){
        this.phoneNumber = phoneNumber;
    }
    public String getAddress(){
        return this.address;
    }
    public void setAddress(String address){
        this.address = address;
    }
    public String getConfirmedPassword() {return confirmedPassword;}
    public void setConfirmedPassword(String confirmedPassword) {this.confirmedPassword = confirmedPassword;}

    public String getClient() {return client;}

    public void setClient(String client) {this.client = client;}
}
