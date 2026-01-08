package com.ubb.validator;

import com.ubb.domain.card.Card;
import com.ubb.domain.card.TipCard;
import com.ubb.exceptions.ValidationException;

/**
 * Validează obiectele de tip Card.
 * <p>
 * Se asigură că:
 *  - ID-ul este pozitiv
 *  - numele cardului nu este gol
 *  - tipul cardului este valid (FLYING sau SWIMMING)
 * </p>
 */
public class CardValidator implements ValidatorStrategy<Card<?>> {

    @Override
    public void validate(Card<?> card) {
        if (card == null) {
            throw new ValidationException("Cardul nu poate fi null!");
        }

        if (card.getId() == null || card.getId() <= 0) {
            throw new ValidationException("ID-ul cardului trebuie să fie un număr pozitiv!");
        }

        if (card.getNumeCard() == null || card.getNumeCard().trim().isEmpty()) {
            throw new ValidationException("Numele cardului nu poate fi gol!");
        }

        TipCard tip = null;
        if (card.getClass().getSimpleName().equals("SkyFlyers"))
            tip = TipCard.FLYING;
        else if (card.getClass().getSimpleName().equals("SwimMasters"))
            tip = TipCard.SWIMMING;

        if (tip == null) {
            throw new ValidationException("Tipul cardului este necunoscut!");
        }
    }
}
