package rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.users;

import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.ProfileField;

import java.time.LocalDateTime;

public class ProfileChangeRequestDTO {
    private Long id;
    private String userEmail;
    private ProfileField field;
    private String oldValue;
    private String newValue;
    private LocalDateTime createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public ProfileField getField() {
        return field;
    }

    public void setField(ProfileField field) {
        this.field = field;
    }

    public String getOldValue() {
        return oldValue;
    }

    public void setOldValue(String oldValue) {
        this.oldValue = oldValue;
    }

    public String getNewValue() {
        return newValue;
    }

    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
