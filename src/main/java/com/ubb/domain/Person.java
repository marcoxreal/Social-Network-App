package com.ubb.domain;

/**
 * Clasa care reprezinta o persoana din reteaua sociala.
 * <p>
 * Extinde clasa abstracta {@link User} si nu adauga atribute suplimentare.
 * Persoanele pot avea prieteni si sunt tratate la fel ca ratele in logica aplicatiei.
 * </p>
 */
public class Person extends User {

    public Person(Long id, String username, String email, String password) {
        super(id, username, email, password);
    }

    @Override
    public String toString() {
        return getUsername();
    }
}

