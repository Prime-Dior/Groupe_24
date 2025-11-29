package com.medipass.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import com.medipass.user.ProfessionnelSante;

/**
 * Consultation programmée ou réalisée.
 * Contient des champs basiques : date/heure, motif, observations, diagnostic, statut.
 */
public class Consultation {
    private static int counter = 1;
    private final int idConsultation;
    private final LocalDateTime dateHeure;
    private final String motif;
    private String observations;
    private String diagnostic;
    private String statut;
    private final ProfessionnelSante professionnel;
    private final Patient patient;
    private int dureeMinutes = 30; // Durée par défaut

    private static final String[] STATUTS_VALIDES = {"programmée", "effectuée", "annulée", "en cours"};

    public Consultation(LocalDateTime dateHeure, String motif, ProfessionnelSante professionnel, Patient patient) {
        this.idConsultation = counter++;
        this.dateHeure = dateHeure;
        this.motif = motif;
        this.professionnel = professionnel;
        this.patient = patient;
        this.statut = "programmée";
    }

    // Getters
    public int getIdConsultation() { return idConsultation; }
    public LocalDateTime getDateHeure() { return dateHeure; }
    public int getDureeMinutes() { return dureeMinutes; }
    public ProfessionnelSante getProfessionnel() { return professionnel; }
    public Patient getPatient() { return patient; }
    public String getStatut() { return statut; }
    public String getMotif() { return motif; }
    public String getObservations() { return observations; }
    public String getDiagnostic() { return diagnostic; }
    
    /**
     * Calcule l'heure de fin de la consultation
     */
    public LocalDateTime getFinConsultation() {
        return dateHeure.plusMinutes(dureeMinutes);
    }

    // Setters avec validation
    public void setDureeMinutes(int dureeMinutes) { 
        if (dureeMinutes > 0) {
            this.dureeMinutes = dureeMinutes; 
        }
    }
    
    public void setObservations(String obs) { 
        this.observations = obs; 
    }
    
    public void setDiagnostic(String d) { 
        this.diagnostic = d; 
    }
    
    public void setStatut(String s) { 
        for (String statut : STATUTS_VALIDES) {
            if (statut.equalsIgnoreCase(s)) {
                this.statut = s;
                return;
            }
        }
        System.err.println("⚠️ Statut invalide : " + s + ". Valeurs acceptées : programmée, effectuée, annulée, en cours");
    }

    /**
     * Vérifie si la consultation est passée
     */
    public boolean estPassee() {
        return LocalDateTime.now().isAfter(this.getFinConsultation());
    }

    /**
     * Vérifie si la consultation est en cours
     */
    public boolean estEnCours() {
        LocalDateTime now = LocalDateTime.now();
        return now.isAfter(dateHeure) && now.isBefore(getFinConsultation());
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return String.format("Consultation[%d] %s (%d min) - %s\n  Professionnel: Dr.%s %s\n  Patient: %s %s\n  Statut: %s",
                idConsultation, 
                dateHeure.format(formatter), 
                dureeMinutes, 
                motif, 
                professionnel.getNom(), 
                professionnel.getPrenom(),
                patient.getNom(), 
                patient.getPrenom(),
                statut);
    }
}