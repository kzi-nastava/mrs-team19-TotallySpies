package rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.auth;

import java.util.Date;

public class UserTokenStateDTO {

    private String accessToken;
    private Date expirationDate;

    public UserTokenStateDTO() {
        this.accessToken = null;
        this.expirationDate = null;
    }

    public UserTokenStateDTO(String accessToken, Date expirationDate) {
        this.accessToken = accessToken;
        this.expirationDate = expirationDate;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }
}
