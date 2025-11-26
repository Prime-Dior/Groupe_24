package com.medipass.model;

/**
 * Patient hérite de Personne et possède un dossier médical.
 * Le dossier est créé automatiquement lors de la création du patient.
 */
public class Patient extends Personne {
    private String numeroSecuriteSociale;
    private String groupeSanguin;
    private final DossierMedical dossier;

    public Patient(int id, String nom, String prenom) {
        super(id, nom, prenom);
        this.dossier = new DossierMedical(this);
    }

    public String getNumeroSecuriteSociale() { return numeroSecuriteSociale; }
    public void setNumeroSecuriteSociale(String numero) { this.numeroSecuriteSociale = numero; }

    public String getGroupeSanguin() { return groupeSanguin; }
    public void setGroupeSanguin(String g) { this.groupeSanguin = g; }

    public DossierMedical getDossierMedical() { return dossier; }
}
