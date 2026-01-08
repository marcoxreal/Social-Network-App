package com.ubb.event;

public interface Observer<E> {
    void update(E mesaj);
}