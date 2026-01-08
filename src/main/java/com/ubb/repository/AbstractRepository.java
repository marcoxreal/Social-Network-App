package com.ubb.repository;

import com.ubb.exceptions.RepositoryException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Clasa abstracta pentru depozit
 * @param <T>
 */
public abstract class AbstractRepository<T> implements Repository<T> {
    private final List<T> elems = new ArrayList<>();
    @Override
    public void add(T elem) {
        Optional<T> existent = findById(getId(elem));
        if (existent.isPresent()) {
            throw new RepositoryException("Element with id " + getId(elem) + " already exists");
        }
        elems.add(elem);
    }
    @Override
    public void remove(Long id) {
        if (elems.isEmpty()) {
            throw new RepositoryException("Nu au fost gasite elemente");
        }
        boolean removed = elems.removeIf(e -> getId(e).equals(id));
        if (!removed) {
            throw new RepositoryException("Nu exista niciun element cu ID-ul " + id + "!");
        }
    }
    @Override
    public Optional<T> findById(Long id) {
        return elems.stream()
                .filter(e -> getId(e).equals(id))
                .findFirst();
    }
    @Override
    public List<T> getAll() {

        return new ArrayList<>(elems);
    }
    protected abstract Long getId(T elem);
    protected List<T> getElems() {
        return elems;
    }
    @Override
    public void update(T elem) {
        Long id = getId(elem);
        for (int i = 0; i < getElems().size(); i++) {
            if (getId(getElems().get(i)).equals(id)) {
                getElems().set(i, elem);
                return;
            }
        }
        throw new RepositoryException("Elementul cu ID " + id + " nu există!");
    }
}
