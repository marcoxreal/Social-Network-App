package com.ubb.event;

import com.ubb.domain.Duck;
import java.util.List;

public class RaceResult {
    private final String eventName;
    private final double bestTime;
    private final List<Duck> ranking;
    private final String message;

    public RaceResult(String eventName, double bestTime, List<Duck> ranking, String message) {
        this.eventName = eventName;
        this.bestTime = bestTime;
        this.ranking = ranking;
        this.message = message;
    }

    public String getEventName() { return eventName; }
    public double getBestTime() { return bestTime; }
    public List<Duck> getRanking() { return ranking; }
    public String getMessage() { return message; }
}
