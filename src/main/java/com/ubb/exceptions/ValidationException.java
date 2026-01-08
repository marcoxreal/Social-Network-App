package com.ubb.exceptions;


/**
 * Exceptie personalizata pentru erorile aparute in nivelul de validare.
 * <p>
 * Aceasta clasa este folosita pentru a semnala probleme logice
 * care apar in timpul operatiilor din servicii (de exemplu,
 * incercarea de a adauga o prietenie invalida sau de a sterge un utilizator inexistent).
 * </p>
 */
public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}
