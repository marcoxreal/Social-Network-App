package com.ubb.factory;

import com.ubb.domain.Person;
import com.ubb.domain.User;
import com.ubb.dto.UserData;
/**
 * Clasa care creeaza obiecte de tip Person.
 */
public class PersonFactory implements UserFactory {
    /**
     * Creeaza un obiect Person pe baza datelor primite.
     *
     * @param data informatiile utilizatorului (id, username)
     * @return obiectul Person creat
     * @throws IllegalArgumentException daca username este null
     */
    @Override
    public User create(UserData data) {
        if (data == null) throw new IllegalArgumentException("UserData nu poate fi null");
        if (data.username == null || data.username.isBlank())
            throw new IllegalArgumentException("Username este obligatoriu pentru persoană!");
        if (data.email == null || data.email.isBlank())
            throw new IllegalArgumentException("Email este obligatoriu pentru persoană!");

        return new Person(
                data.id,
                data.username,
                data.email,
                data.passwordHash
        );
    }
}
