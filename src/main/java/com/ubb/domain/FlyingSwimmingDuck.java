package com.ubb.domain;

public class FlyingSwimmingDuck extends Duck implements Zburator, Inotator {

    public FlyingSwimmingDuck(Long id, String username, String email, String passwordHash, double viteza, double rezistenta) {
        super(id, username, email, passwordHash, TipRata.FLYING_AND_SWIMMING, viteza, rezistenta);
    }

    @Override
    public void zboara() {
        System.out.println(getUsername() + " zboară elegant deasupra apei!");
    }

    @Override
    public void inoata() {
        System.out.println(getUsername() + " înoată rapid!");
    }

    @Override
    public void participaLaEveniment() {
        System.out.println(getUsername() + " participă la o cursă combinată zbor + înot!");
    }
}
