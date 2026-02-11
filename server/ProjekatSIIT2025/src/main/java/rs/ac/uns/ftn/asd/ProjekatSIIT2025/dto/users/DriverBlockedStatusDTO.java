package rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.users;

public class DriverBlockedStatusDTO {
    private boolean blocked;
    private String  blockReason;

    public boolean getBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    public String getBlockReason() {
        return blockReason;
    }

    public void setBlockReason(String blockReason) {
        this.blockReason = blockReason;
    }
}
