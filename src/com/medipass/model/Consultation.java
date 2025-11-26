package com.medipass.model;

import java.time.LocalDateTime;
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

    public Consultation(LocalDateTime dateHeure, String motif, ProfessionnelSante professionnel, Patient patient) {
        this.idConsultation = counter++;
        this.dateHeure = dateHeure;
        this.motif = motif;
        this.professionnel = professionnel;
        this.patient = patient;
        this.statut = "programmée";
    }

    public int getIdConsultation() { return idConsultation; }
    public LocalDateTime getDateHeure(){ return dateHeure; }
    public int getDureeMinutes() { return dureeMinutes; }
    public void setDureeMinutes(int dureeMinutes) { this.dureeMinutes = dureeMinutes; }
    
    public LocalDateTime getFinConsultation() {
        return dateHeure.plusMinutes(dureeMinutes);
    }

    public ProfessionnelSante getProfessionnel(){ return professionnel; }
    public Patient getPatient(){ return patient; }
    public String getStatut(){ return statut; }
    public String getMotif(){ return motif; }
    public String getObservations(){ return observations; }
    public String getDiagnostic(){ return diagnostic; }

    public void setObservations(String obs){ this.observations = obs; }
    public void setDiagnostic(String d){ this.diagnostic = d; }
    public void setStatut(String s){ this.statut = s; }

    @Override
    public String toString() {
        return String.format("Consultation[%d] %s (%d min) - %s avec Dr:%s pour patient:%s statut:%s",
                idConsultation, dateHeure, dureeMinutes, motif, professionnel.getLoginID(), patient.getNom(), statut);
    }
}
