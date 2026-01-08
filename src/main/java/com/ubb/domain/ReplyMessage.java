package com.ubb.domain;

import java.time.LocalDateTime;

public class ReplyMessage extends Message{
    private Message originalMessage;

    public ReplyMessage(Long id, User from, User to, String message, LocalDateTime date, Message originalMessage) {
        super(id, from, to, message, date);
        this.originalMessage = originalMessage;
    }

    public Message getOriginalMessage() {
        return originalMessage;
    }

    @Override
    public String toString(){
        return getMessage();
    }
}

