package rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto;

public class UserLoginResponseDTO {
    private String name;
    private String lastName;

    public UserLoginResponseDTO(){}


    public void setName(String name) {
        this.name = name;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getName() {
        return name;
    }

    public String getLastName() {
        return lastName;
    }
}
