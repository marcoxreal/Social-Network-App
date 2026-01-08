package com.ubb.domain.card;

import com.ubb.domain.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Clasa abstracta care reprezinta un grup (card) de rate cu scop comun.
 * <p>
 * Fiecare card are un nume si o lista de membri de tip {@link Duck}.
 * </p>
 *
 * @param <T> tipul de rață din acest card (ex: {@link SwimmingDuck}, {@link FlyingDuck})
 */
public abstract class Card<T extends Duck> {

    /** ID unic al cardului */
    private final Long id;

    /** Numele cardului (ex: SwimMasters, SkyFlyers) */
    private String numeCard;

    /** Lista de membri (rațe) care fac parte din card */
    private final List<T> membri;

    /** doar pentru stocarea ID-urilor membrilor înainte de reconstrucție */



    public Card(Long id, String numeCard) {
        this.id = id;
        this.numeCard = numeCard;
        this.membri = new ArrayList<>();
    }

    /**
     * Adaugă o rață în card și setează automat cardul acesteia.
     */
    public void adaugaMembru(T duck) {
        if (!membri.contains(duck)) {
            membri.add(duck);
            duck.setCard(this);
        }
    }

    /**
     * Elimină o rață din card și șterge asocierea acesteia cu cardul.
     */
    public void eliminaMembru(T duck) {
        membri.remove(duck);
        if (duck.getCard() == this) {
            duck.setCard(null);
        }
    }

    /**
     * Calculează performanța medie a cardului
     * ca media aritmetică a (viteza + rezistența) membrilor.
     */
    public PerformantaCard getPerformantaMedie() {
        if (membri.isEmpty()) {
            return new PerformantaCard(0.0, 0.0);
        }

        double sumaViteze = 0;
        double sumaRezistente = 0;

        for (Duck d : membri) {
            sumaViteze += d.getViteza();
            sumaRezistente += d.getRezistenta();
        }

        double vitezaMedie = sumaViteze / membri.size();
        double rezistentaMedie = sumaRezistente / membri.size();

        return new PerformantaCard(vitezaMedie, rezistentaMedie);
    }


    public List<T> getMembri() {
        return membri;
    }

    public String getNumeCard() {
        return numeCard;
    }

    public void setNumeCard(String numeCard) {
        this.numeCard = numeCard;
    }

    public Long getId() {
        return id;
    }




    @Override
    public String toString() {
        return "Card{" +
                "id=" + id +
                ", numeCard='" + numeCard + '\'' +
                ", membri=" + membri.stream().map(Duck::getUsername).toList() +
                ", performantaMedie=" + String.format("%.2f", getPerformantaMedie()) +
                '}';
    }
}
