package com.example;

public class Kraj {
    public int id;
    public String ime;
    public String postna;
    public String velPorabnik; // Now a String instead of boolean

    public Kraj(int id, String ime, String postna, String velPorabnik) {
        this.id = id;
        this.ime = ime;
        this.postna = postna;
        this.velPorabnik = velPorabnik;
    }

    @Override
    public String toString() {
        String s = "";
        s += "[" + postna + "] " + ime;

        if (velPorabnik.length() > 0)
            s += " (" + velPorabnik + ")";
        return s;
    }
}
