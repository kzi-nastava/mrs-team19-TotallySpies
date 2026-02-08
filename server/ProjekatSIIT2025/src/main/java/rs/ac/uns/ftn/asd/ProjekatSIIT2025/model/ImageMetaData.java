package rs.ac.uns.ftn.asd.ProjekatSIIT2025.model;

public class ImageMetaData {
    private String contentType;
    private Long contentId;
    private String userEmail;

    public ImageMetaData() {}

    public ImageMetaData(String contentType, Long contentId){
        this.contentType = contentType;
        this.contentId = contentId;
        this.userEmail = null;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Long getContentId() {
        return contentId;
    }

    public void setContentId(Long contentId) {
        this.contentId = contentId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
}
