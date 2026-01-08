package com.ubb.domain.card;

public class PerformantaCard {
    private final double vitezaMedie;
    private final double rezistentaMedie;

    public PerformantaCard(double vitezaMedie, double rezistentaMedie) {
        this.vitezaMedie = vitezaMedie;
        this.rezistentaMedie = rezistentaMedie;
    }

    public double getVitezaMedie() {
        return vitezaMedie;
    }

    public double getRezistentaMedie() {
        return rezistentaMedie;
    }

    @Override
    public String toString() {
        return String.format("vitezaMedie=%.2f, rezistentaMedie=%.2f",
                vitezaMedie, rezistentaMedie);
    }
}
