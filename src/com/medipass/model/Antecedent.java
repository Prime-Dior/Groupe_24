package com.medipass.model;

import java.time.LocalDate;

/**
 * Représente un antécédent médical (simple).
 */
public class Antecedent {
    private static int counter=1;
    private final int idAntecedent;
    private final String type;
    private final LocalDate date;
    private final String gravite;
    private final boolean actif;

    public Antecedent(String type, String description, LocalDate date, String gravite, boolean actif) {
        this.idAntecedent = counter++;
        this.type = type;
        this.date = date;
        this.gravite = gravite;
        this.actif = actif;
    }

    public int getIdAntecedent() { return idAntecedent; }
    @Override
    public String toString(){
        return String.format("[%d] %s (%s) - %s - actif:%s", idAntecedent, type, date, gravite, actif);
    }
}
