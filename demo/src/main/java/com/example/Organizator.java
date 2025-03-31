package com.example;

public class Organizator {
    public int id;
    public String ime;
    public String opis;
    public String email;
    public String pasw;
    public String telefon;
    public String naslov;
    public int krajId;
    public int settingsId;
    public int stDogodkov;

    public Organizator(int id, String ime, String opis, String email, String pasw, String telefon, String naslov,
            int krajId,
            int settingsId, int stDogodkov) {
        this.id = id;
        this.ime = ime;
        this.opis = opis;
        this.email = email;
        this.pasw = pasw;
        this.telefon = telefon;
        this.naslov = naslov;
        this.krajId = krajId;
        this.settingsId = settingsId;
        this.stDogodkov = stDogodkov;
    }

    @Override
    public String toString() {
        return "Organizator{" +
                "id=" + id +
                ", ime='" + ime + '\'' +
                ", opis='" + opis + '\'' +
                ", email='" + email + '\'' +
                ", pasw='" + pasw + '\'' +
                ", telefon='" + telefon + '\'' +
                ", naslov='" + naslov + '\'' +
                ", krajId=" + krajId +
                ", settingsId=" + settingsId +
                ", stDogodkov=" + stDogodkov +
                '}';
    }
}