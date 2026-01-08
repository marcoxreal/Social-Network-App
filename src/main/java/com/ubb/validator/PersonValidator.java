package com.ubb.validator;

import com.ubb.domain.Person;
import com.ubb.domain.User;
import com.ubb.exceptions.ValidationException;

public class PersonValidator implements ValidatorStrategy<User> {
    @Override
    public void validate(User entity) {
        if (entity instanceof Person person) {
            if (person.getUsername() == null || person.getUsername().isEmpty())
                throw new ValidationException("Username invalid!");
        }
    }
}

