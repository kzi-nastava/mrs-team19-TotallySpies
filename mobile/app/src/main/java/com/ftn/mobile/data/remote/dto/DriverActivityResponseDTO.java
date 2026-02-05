package com.ftn.mobile.data.remote.dto;

public class DriverActivityResponseDTO {
    private long minutesLast24h;

    public DriverActivityResponseDTO() {}

    public long getMinutesLast24h() {
        return minutesLast24h;
    }

    public void setMinutesLast24h(long minutesLast24h) {
        this.minutesLast24h = minutesLast24h;
    }
}
