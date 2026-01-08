package com.ubb.event;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractObservable<E>
        implements Observable<E> {

    protected final List<Observer<E>> observers = new ArrayList<>();

    @Override
    public void addObserver(Observer<E> obs) {
        if (!observers.contains(obs))
            observers.add(obs);
    }

    @Override
    public void removeObserver(Observer<E> obs) {
        observers.remove(obs);
    }

    @Override
    public void notifyObservers(E event) {
        observers.forEach(o -> o.update(event));
    }
}
