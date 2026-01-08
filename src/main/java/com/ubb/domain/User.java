package com.ubb.domain;

import com.ubb.event.Observer;

import java.util.HashSet;
import java.util.Set;

/**
 * Clasa abstracta care reprezinta un utilizator generic din retea.
 * <p>
 * Fiecare utilizator are un ID unic, un nume de utilizator si o colectie de prieteni
 * identificati prin ID-urile lor. Clasele derivate (ex: {@link Person}, {@link Duck})
 * vor implementa metoda {@link #toString()} pentru afisarea personalizata.
 */
public abstract class User {
    private Long id;
    private String username;
    private String email;
    private String passwordHash;
    private Set<Long> friends;
    public User(Long id, String username, String email, String passwordHash) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.friends = new HashSet<>();
    }
    public Long getId() {
        return id;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public Set<Long> getFriends() {
        return friends;
    }
    public void addFriend(Long friendId) {
        friends.add(friendId);
    }
    public void removeFriend(Long friendId) {
        friends.remove(friendId);
    }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    @Override
    public abstract String toString();
}
