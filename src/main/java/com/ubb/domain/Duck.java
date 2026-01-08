package com.ubb.domain;
import com.ubb.domain.card.Card;
/**
 * Clasa care reprezinta o rata din retea.
 * <p>
 * O rata este un tip special de {@link User}, caracterizata suplimentar printr-un {@link TipRata}.
 * Tipul ratei defineste comportamentul acesteia (ex: FLYING, SWIMMING, FLYING_AND_SWIMMING).
 */
public  abstract class  Duck extends User {

    /** Tipul ratei (ex: FLYING, SWIMMING, FLYING_AND_SWIMMING) */
    private TipRata tip;
    private double viteza;
    private double rezistenta;
    private Card<? extends Duck> card;


    /**
     * Constructor pentru crearea unei rate.
     *
     * @param id        ID-ul unic al ratei
     * @param username  numele ratei
     * @param tip       tipul ratei (vezi {@link TipRata})
     * @param viteza    viteza ratei
     */
    public Duck(Long id, String username, String email, String passwordHash, TipRata tip, double viteza, double rezistenta) {
        super(id, username,  email, passwordHash);
        this.tip = tip;
        this.viteza = viteza;
        this.rezistenta = rezistenta;
    }

    /**
     * Returneaza tipul ratei.
     *
     * @return valoarea de tip {@link TipRata} asociata acestei rate
     */
    public TipRata getTip() {
        return tip;
    }

    /**
     * Seteaza tipul ratei.
     *
     * @param tip noul {@link TipRata} care va fi asociat acestei rate
     */
    public void setTip(TipRata tip) {
        this.tip = tip;
    }


    public double getViteza() {
        return viteza;
    }

    public void setViteza(double viteza) {
        this.viteza = viteza;
    }

    public double getRezistenta() {
        return rezistenta;
    }

    public void setRezistenta(double rezistenta) {
        this.rezistenta = rezistenta;
    }

    public abstract void participaLaEveniment();



    /**
     * Seteaza cardul (grupul) din care face parte aceasta rata.
     * Aceasta metoda este apelata automat cand rata este adaugata intr-un card.
     *
     * @param card obiectul {@link Card} in care este inscrisa rata
     */
    public void setCard(Card<? extends Duck> card) {
        this.card = card;
    }

    public Card<? extends Duck> getCard() {
        return card;
    }

    @Override
    public String toString() {
        return getUsername();
    }
}
