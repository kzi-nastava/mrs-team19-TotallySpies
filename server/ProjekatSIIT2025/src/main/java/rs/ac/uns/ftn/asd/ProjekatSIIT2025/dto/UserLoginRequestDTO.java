package rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto;

public class UserLoginRequestDTO {
    private String email;
    private String password;

    public UserLoginRequestDTO(){}

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

}
