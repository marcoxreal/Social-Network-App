package com.ubb.validator;

/**
 * Interfata pentru strategii de validare.
 * <p>
 * Permite definirea mai multor reguli de validare pentru entitati diferite.
 * </p>
 *
 * @param <T> tipul obiectului care trebuie validat
 */
public interface ValidatorStrategy<T> {
    /**
     * Valideaza o entitate data.
     * <p>
     * Arunca o exceptie daca entitatea nu respecta regulile de validare.
     * </p>
     * @param entity entitatea care trebuie validata
     * @throws IllegalArgumentException daca entitatea este invalida
     */
    void validate(T entity);
}
