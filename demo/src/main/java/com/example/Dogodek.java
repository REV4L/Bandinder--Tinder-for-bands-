package com.example;

import java.sql.Timestamp;

public class Dogodek {
    public int id;
    public String ime;
    public String opis;
    public int organizator_id; // Replaced organizator_ime with organizator_id
    public int prostor_id; // Replaced prostor_naziv with prostor_id
    public int izvajalec_id; // Replaced izvajalec_ime with izvajalec_id
    public float cena_vstopnice;
    public Timestamp cas;

    // Constructor to initialize all properties
    public Dogodek(int id, String ime, String opis, int organizator_id, int prostor_id,
            int izvajalec_id, float cena_vstopnice, Timestamp cas) {
        this.id = id;
        this.ime = ime;
        this.opis = opis;
        this.organizator_id = organizator_id;
        this.prostor_id = prostor_id;
        this.izvajalec_id = izvajalec_id;
        this.cena_vstopnice = cena_vstopnice;
        this.cas = cas;
    }

    @Override
    public String toString() {
        return ime; // Vrne samo ime prostora
    }
}
