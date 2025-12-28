package rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto;

public class UpdateUserPasswordRequestDTO {
    private String oldPassword;
    private String newPassword;

    public UpdateUserPasswordRequestDTO() {}

    public String getNewPassword(){
        return this.newPassword;
    }
    public void setNewPassword(String password){
        this.newPassword = password;
    }
    public String getOldPassword(){
        return this.oldPassword;
    }
    public void setOldPassword(String password){
        this.oldPassword = password;
    }



}
