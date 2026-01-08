package com.ubb.service;

import com.ubb.domain.Message;
import com.ubb.domain.ReplyMessage;
import com.ubb.domain.User;
import com.ubb.event.AbstractObservable;
import com.ubb.event.MessageEvent;
import com.ubb.repository.MessageRepositoryDB;

import java.time.LocalDateTime;
import java.util.List;

public class MessageService extends AbstractObservable<MessageEvent> {

    private final MessageRepositoryDB messageRepo;

    public MessageService(MessageRepositoryDB repo) {
        this.messageRepo = repo;
    }

    public Message sendMessage(User from, User to, String text) {
        Message msg = new Message(null, from, to, text, LocalDateTime.now());
        Message saved = messageRepo.save(msg);
        notifyObservers(new MessageEvent(saved));
        return saved;
    }

    public ReplyMessage sendReply(User from, User to, String text, Message original) {
        ReplyMessage msg = new ReplyMessage(null, from, to, text, LocalDateTime.now(), original);
        ReplyMessage saved = (ReplyMessage) messageRepo.save(msg);
        notifyObservers(new MessageEvent(saved));
        return saved;
    }

    public List<Message> getConversation(Long user1, Long user2) {
        return messageRepo.getConversation(user1, user2);
    }
}
