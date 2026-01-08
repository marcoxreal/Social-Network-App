package com.ubb.domain;

public class SwimmingDuck extends Duck implements Inotator {

    public SwimmingDuck(Long id, String username, String email, String passwordHash, double viteza, double rezistenta) {
        super(id, username, email, passwordHash, TipRata.SWIMMING, viteza, rezistenta);
    }

    @Override
    public void inoata() {
        System.out.println(getUsername() + " înoată cu viteza " + getViteza() + " m/s");
    }

    @Override
    public void participaLaEveniment() {
        System.out.println(getUsername() + " participă la un eveniment de înot!");
    }
}
