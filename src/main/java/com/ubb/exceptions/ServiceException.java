package com.ubb.exceptions;

/**
 * Exceptie personalizata pentru erorile aparute in nivelul de service.
 * <p>
 * Aceasta clasa este folosita pentru a semnala probleme logice
 * care apar in timpul operatiilor din servicii (de exemplu,
 * incercarea de a adauga o prietenie invalida sau de a sterge un utilizator inexistent).
 * </p>
 */
public class ServiceException extends RuntimeException {
    /**
     * Creeaza o exceptie specifica serviciilor aplicatiei.
     * @param message mesajul de eroare explicativ
     */
    public ServiceException(String message) {
        super(message);
    }
}
