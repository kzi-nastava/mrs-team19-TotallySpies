package rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto;

import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.AcceptanceState;

public class EmailVerificationResponseDTO {
    private String message;
    private AcceptanceState status;

    public AcceptanceState getStatus() {
        return status;
    }

    public void setStatus(AcceptanceState status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
