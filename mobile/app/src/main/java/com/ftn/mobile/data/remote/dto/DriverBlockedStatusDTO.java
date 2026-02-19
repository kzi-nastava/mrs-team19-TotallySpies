package com.ftn.mobile.data.remote.dto;

public class DriverBlockedStatusDTO {
    private boolean blocked;
    private String blockReason;

    public boolean isBlocked() {
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
