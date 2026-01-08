package com.ubb.repository;

import java.util.List;
import java.util.Optional;

/**
 * Interfata generica pentru depozit
 */
public interface Repository<T> {

    void add(T elem);
    void remove(Long id);
    Optional<T> findById(Long id);
    List<T> getAll();
    void update(T elem);
}
