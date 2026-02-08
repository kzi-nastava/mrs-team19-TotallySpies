package rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.auth;

import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.User;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.UserRole;

public class UserRegisterResponseDTO {
    private Long id;
    private String email;
    private String name;
    private String lastName;
    private String profilePicture;
    private String phoneNumber;
    private String address;
    private UserRole role;

    public UserRegisterResponseDTO(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.name = user.getName();
        this.lastName = user.getLastName();
        this.profilePicture = user.getProfilePicture();
        this.phoneNumber = user.getPhoneNumber();
        this.address = user.getAddress();
        this.role = user.getRole();
    }

    public Long getId(){
        return this.id;
    }
    public void setId(Long id){
        this.id = id;
    }
    public String getEmail(){
        return this.email;
    }
    public void setEmail(String email){
        this.email = email;
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
    public String getProfilePicture(){
        return this.profilePicture;
    }
    public void setProfilePicture(String profilePicture){
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
    public UserRole getRole(){ return this.role; }
    public void setRole(UserRole role){ this.role = role; }
}
