package com.example;

public class Prostor {
    public int id;
    public String ime;
    public String opis;
    public int kapaciteta;
    public String naslov;
    public int krajId;
    public int stDogodkov;

    public Prostor(int id, String ime, String opis, int kapaciteta, String naslov, int krajId, int stDogodkov) {
        this.id = id;
        this.ime = ime;
        this.opis = opis;
        this.kapaciteta = kapaciteta;
        this.naslov = naslov;
        this.krajId = krajId;
        this.stDogodkov = stDogodkov;
    }

    @Override
    public String toString() {
        return ime + " [" + stDogodkov + "]" + " [max:" + kapaciteta + "]";
    }
}
