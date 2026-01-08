package com.ubb.domain;

import java.time.LocalDateTime;

public class FriendRequest {

    private Long id;
    private User from;
    private User to;
    private FriendRequestStatus status;
    private LocalDateTime date;

    public FriendRequest(Long id, User from, User to,
                         FriendRequestStatus status,
                         LocalDateTime date) {
        this.id = id;
        this.from = from;
        this.to = to;
        this.status = status;
        this.date = date;
    }

    public Long getId() { return id; }
    public User getFrom() { return from; }
    public User getTo() { return to; }
    public FriendRequestStatus getStatus() { return status; }
    public LocalDateTime getDate() { return date; }

    public void setStatus(FriendRequestStatus status) {
        this.status = status;
    }
}
