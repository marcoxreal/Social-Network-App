package com.ubb.event;

import java.time.LocalDateTime;

public class RaceEventEvent {
    private final String raceName;
    private final String message;
    private final LocalDateTime time = LocalDateTime.now();

    public RaceEventEvent(String raceName, String message) {
        this.raceName = raceName;
        this.message = message;
    }

    public String getRaceName() { return raceName; }
    public String getMessage() { return message; }
    public LocalDateTime getTime() { return time; }
}
