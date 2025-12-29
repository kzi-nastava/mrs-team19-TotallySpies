package rs.ac.uns.ftn.asd.ProjekatSIIT2025.model;

public class Report {
    private Long id;
    private Long passengerId;
    private String reportReason;

    public Report(Long id, Long passengerId, String reportReason) {
        this.id = id;
        this.passengerId = passengerId;
        this.reportReason = reportReason;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPassengerId() {
        return passengerId;
    }

    public void setPassengerId(Long passengerId) {
        this.passengerId = passengerId;
    }

    public String getReportReason() {
        return reportReason;
    }

    public void setReportReason(String reportReason) {
        this.reportReason = reportReason;
    }
}
