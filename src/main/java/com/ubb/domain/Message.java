package com.ubb.domain;

import java.time.LocalDateTime;

public class Message {
    private Long id;
    private User from;
    private User to;
    private String message;
    private LocalDateTime date;
    public Message(Long id, User from, User to, String message, LocalDateTime date) {
        this.id = id;
        this.from = from;
        this.to = to;
        this.message = message;
        this.date = date;
    }

    public Long getId() { return id; }
    public User getFrom() { return from; }
    public User getTo() { return to; }
    public String getMessage() { return message; }
    public LocalDateTime getDate() { return date; }
    public void setId(Long id) { this.id = id; }

    @Override
    public String toString() {
        return message;
    }
}
