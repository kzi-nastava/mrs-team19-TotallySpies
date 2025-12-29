package rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.users;

public class DriverRegisterRequestDTO {
    private String email;
    private String name;
    private String lastName;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
