package com.ubb.factory;

import com.ubb.domain.Duck;
import com.ubb.domain.card.*;

public class CardFactory {
    public Card<? extends Duck> create(Long id, String numeCard, TipCard tip) {
        return switch (tip) {
            case FLYING -> new SkyFlyers(id, numeCard);
            case SWIMMING -> new SwimMasters(id, numeCard);
        };
    }
}
