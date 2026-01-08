package com.ubb.domain;

public class ConcreteDuck extends Duck {

    public ConcreteDuck(Long id, String username, String email, String passwordHash,
                        TipRata tip, double viteza, double rezistenta) {
        super(id, username, email, passwordHash, tip, viteza, rezistenta);
    }

    @Override
    public void participaLaEveniment() {
        // implementează comportamentul specific
        System.out.println("Rata participa la eveniment!");
    }
}
