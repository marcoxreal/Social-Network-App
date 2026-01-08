package com.ubb.validator;

import com.ubb.domain.Duck;
import com.ubb.domain.User;
import com.ubb.exceptions.ValidationException;

public class DuckValidator implements ValidatorStrategy<User> {
    @Override
    public void validate(User entity) {
        if (entity instanceof Duck duck) {

            if (duck.getUsername() == null || duck.getUsername().isEmpty())
                throw new ValidationException("Username invalid pentru rață!");

            if (duck.getTip() == null)
                throw new ValidationException("Tipul raței lipsește!");

            if (Double.isNaN(duck.getViteza()) || duck.getViteza() <= 0) {
                throw new ValidationException("Viteza trebuie să fie un număr pozitiv!");
            }


            if (Double.isNaN(duck.getRezistenta()) || duck.getRezistenta() <= 0) {
                throw new ValidationException("Rezistența trebuie să fie un număr pozitiv!");
            }
        }
    }
}
