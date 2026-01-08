package com.ubb.domain;

public class FlyingDuck extends Duck implements Zburator {

    public FlyingDuck(Long id, String username, String email, String passwordHash, double viteza, double rezistenta) {
        super(id, username, email, passwordHash, TipRata.FLYING, viteza, rezistenta);
    }

    @Override
    public void zboara() {
        System.out.println(getUsername() + " zboară cu viteza " + getViteza() + " m/s");
    }

    @Override
    public void participaLaEveniment() {
        System.out.println(getUsername() + " participă la un eveniment de zbor!");
    }
}
