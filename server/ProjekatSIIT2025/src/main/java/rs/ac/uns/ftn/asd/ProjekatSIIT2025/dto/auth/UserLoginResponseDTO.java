package rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.auth;

import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.UserRole;

public class UserLoginResponseDTO {
    private Long id;
    private String jwt;
    private UserRole role;

    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
