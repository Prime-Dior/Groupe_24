package com.medipass.model;

import java.time.LocalDate;

/**
 * Représente un antécédent médical.
 * Contient le type, une description, la date, la gravité et si l'antécédent est toujours actif.
 */
public class Antecedent {
    private static int counter = 1;
    private final int idAntecedent;
    private final String type;
    private final String description;
    private final LocalDate date;
    private final String gravite;
    private final boolean actif;

    /**
     * Constructeur complet d'un antécédent
     * @param type Type d'antécédent (allergie, maladie chronique, intervention, etc.)
     * @param description Description détaillée
     * @param date Date de survenue ou de diagnostic
     * @param gravite Niveau de gravité (bénin, modéré, grave)
     * @param actif Si l'antécédent est toujours actif/pertinent
     */
    public Antecedent(String type, String description, LocalDate date, String gravite, boolean actif) {
        this.idAntecedent = counter++;
        this.type = type;
        this.description = description;
        this.date = date;
        this.gravite = gravite;
        this.actif = actif;
    }

    // Getters
    public int getIdAntecedent() { return idAntecedent; }
    public String getType() { return type; }
    public String getDescription() { return description; }
    public LocalDate getDate() { return date; }
    public String getGravite() { return gravite; }
    public boolean isActif() { return actif; }

    @Override
    public String toString() {
        return String.format("[%d] %s - %s (%s) - gravité:%s - %s", 
            idAntecedent, 
            type, 
            description != null ? description : "Pas de description", 
            date, 
            gravite, 
            actif ? "actif" : "inactif");
    }
}