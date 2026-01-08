package com.ubb.dto;

import com.ubb.domain.TipRata;

public class UserData {
    public Long id;
    public String username;
    public String email;
    public String passwordHash;
    public String ocupatie;
    public int nivelEmpatie;
    public TipRata tip;
    public double viteza;
    public double rezistenta;


    public UserData() {}
}
