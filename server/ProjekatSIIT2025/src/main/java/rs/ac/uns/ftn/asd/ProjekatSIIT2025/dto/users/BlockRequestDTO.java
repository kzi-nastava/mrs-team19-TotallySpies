package rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.users;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class BlockRequestDTO {
    @NotBlank(message = "Reason for blocking is mandatory")
    @Size(min = 5, message = "Reason must be at least 5 characters long")
    String reason;

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
