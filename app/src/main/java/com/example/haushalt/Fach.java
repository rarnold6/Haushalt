package com.example.haushalt;

import com.example.haushalt.activity.ObjectCollections;

import java.util.Iterator;
import java.util.LinkedList;

public class Fach {
    private long id;
    private int fachNummer;
    private String beschriftung;
    private Gefrierschrank gefrierschrank;

    public Fach(long id, int fachNummer, String beschriftung, Gefrierschrank gefrierschrank){
        this.id = id;
        this.fachNummer = fachNummer;
        this.beschriftung = beschriftung;
        this.gefrierschrank = gefrierschrank;
    }

    public Fach(int fachNummer, String beschriftung, Gefrierschrank gefrierschrank){
        findID();
        this.fachNummer = fachNummer;
        this.beschriftung = beschriftung;
        this.gefrierschrank = gefrierschrank;
    }

    public Fach(int fachNummer, Gefrierschrank gefrierschrank){
        findID();
        this.fachNummer = fachNummer;
        this.beschriftung = "";
        this.gefrierschrank = gefrierschrank;
    }

    private void findID(){
        LinkedList<Fach> faecher = ObjectCollections.getFaecher();
        this.id = faecher.size() + 1;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getFachNummer() {
        return fachNummer;
    }

    public void setFachNummer(int fachNummer) {
        this.fachNummer = fachNummer;
    }

    public String getBeschriftung() {
        return beschriftung;
    }

    public void setBeschriftung(String beschriftung) {
        this.beschriftung = beschriftung;
    }

    public Gefrierschrank getGefrierschrank() {
        return gefrierschrank;
    }

    public void setGefrierschrank(Gefrierschrank gefrierschrank) {
        this.gefrierschrank = gefrierschrank;
    }
}
