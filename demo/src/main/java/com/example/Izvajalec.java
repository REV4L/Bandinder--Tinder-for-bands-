package com.example;

public class Izvajalec {
    public int id;
    public String ime;
    public String opis;
    public String telefon;
    public int stDogodkov; // Calculated using trigger

    public Izvajalec(int id, String ime, String opis, String telefon, int stDogodkov) {
        this.id = id;
        this.ime = ime;
        this.opis = opis;
        this.telefon = telefon;
        this.stDogodkov = stDogodkov;
    }

    @Override
    public String toString() {
        return ime + " [" + stDogodkov + "]"; // Vrne samo ime prostora
    }
}
