package com.ubb.factory;

import com.ubb.domain.User;
import com.ubb.dto.UserData;

/**
 * Interfata pentru crearea obiectelor de tip User.
 */
public interface    UserFactory {

    /**
     * Creeaza un obiect User pe baza datelor primite.
     *
     * @param data informatiile utilizatorului (id, username, tip, etc.)
     * @return obiectul User creat
     */
    User create(UserData data);
}
