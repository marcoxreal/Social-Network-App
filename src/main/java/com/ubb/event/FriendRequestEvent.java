package com.ubb.event;

import com.ubb.domain.FriendRequest;

public class FriendRequestEvent implements Event {
    private final FriendRequest request;

    public FriendRequestEvent(FriendRequest request) {
        this.request = request;
    }

    public FriendRequest getRequest() {
        return request;
    }
}
