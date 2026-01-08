package com.ubb.validator;
/**
 * Clasa care gestioneaza strategia de validare folosind pattern-ul Strategy.
 * <p>
 * Permite schimbarea dinamica a regulilor de validare pentru tipuri diferite de entitati.
 * </p>
 *
 * @param <T> tipul entitatii care urmeaza sa fie validata
 */
public class ValidationContext<T> {
    private ValidatorStrategy<T> strategy;


    /**
     * Creeaza un context de validare fara o strategie initiala.
     * Strategia trebuie setata ulterior prin setStrategy(ValidatorStrategy).
     */
    public ValidationContext() {
        this.strategy = null;
    }

    /**
     * Creeaza un context de validare cu o strategie data.
     * @param strategy strategia de validare initiala
     */
    public ValidationContext(ValidatorStrategy<T> strategy) {
        this.strategy = strategy;
    }

    /**
     * Seteaza o noua strategie de validare.
     * @param strategy strategia care va fi folosita la urmatoarea validare
     */
    public void setStrategy(ValidatorStrategy<T> strategy) {
        this.strategy = strategy;
    }

    /**
     * Executa validarea entitatii curente folosind strategia activa.
     * @param entity entitatea care urmeaza sa fie validata
     * @throws IllegalStateException daca strategia nu a fost setata
     * @throws IllegalArgumentException daca entitatea este invalida
     */
    public void executeValidation(T entity) {
        strategy.validate(entity);
    }
}
