package com.ubb.factory;

import com.ubb.domain.*;
import com.ubb.dto.UserData;

public class DuckFactory implements UserFactory {

    @Override
    public User create(UserData data) {
        if (data.tip == null) {
            throw new IllegalArgumentException("Tipul raței este obligatoriu!");
        }
        if (data.id == null) {
            throw new IllegalArgumentException("Id-ul este obligatoriu!");
        }

        switch (data.tip) {
            case SWIMMING:
                return new SwimmingDuck(
                        data.id,          // Long
                        data.username,       // TipRata
                        data.email,
                        data.passwordHash,
                        data.viteza,
                        data.rezistenta
                );
            case FLYING:
                return new FlyingDuck(
                        data.id,
                        data.username,
                        data.email,
                        data.passwordHash,
                        data.viteza,
                        data.rezistenta
                );
            case FLYING_AND_SWIMMING:
                return new FlyingSwimmingDuck(
                        data.id,
                        data.username,
                        data.email,
                        data.passwordHash,
                        data.viteza,
                        data.rezistenta
                );
            default:
                throw new IllegalArgumentException("Tip necunoscut de rață: " + data.tip);
        }
    }
}
