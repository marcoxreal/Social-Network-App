package com.ubb.event;

import com.ubb.domain.Message;

public class MessageEvent implements Event {
    private final Message message;

    public MessageEvent(Message message) {
        this.message = message;
    }

    public Message getMessage() {
        return message;
    }
}

