package com.medipass.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Dossier médical simple : liste d'antécédents et de consultations.
 * Met à jour la date de dernière modification à chaque changement.
 */
public class DossierMedical {
    private static int counter = 1;
    private final int idDossier;
    private final Patient patient;
    private final LocalDateTime dateCreation;
    private final List<Antecedent> antecedents = new ArrayList<>();
    private final List<Consultation> consultations = new ArrayList<>();

    public DossierMedical(Patient patient) {
        this.idDossier = counter++;
        this.patient = patient;
        this.dateCreation = LocalDateTime.now();
    }

    public int getIdDossier() { return idDossier; }
    public Patient getPatient() { return patient; }
    public LocalDateTime getDateCreation() { return dateCreation; }

    public void ajouterAntecedent(Antecedent a) {
        antecedents.add(a);
    }

    public void ajouterConsultation(Consultation c) {
        consultations.add(c);
    }

    public List<Antecedent> getAntecedents() { return antecedents; }
    public List<Consultation> getConsultations() { return consultations; }

    // Fournit un historique textuel simple pour affichage console
    public List<String> getHistorique() {
        List<String> out = new ArrayList<>();
        for (Consultation c : consultations) {
            out.add(c.toString());
        }
        for (Antecedent a : antecedents) {
            out.add("Antecedent: " + a.toString());
        }
        return out;
    }
}
