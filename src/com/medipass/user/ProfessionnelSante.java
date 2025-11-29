package com.medipass.user;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

import com.medipass.model.Antecedent;
import com.medipass.model.Consultation;
import com.medipass.model.Patient;

/**
 * Professionnel de santé (médecin, infirmier, pharmacien, etc.).
 * Possède un planning simple (liste de consultations) et peut gérer les antécédents.
 */
public class ProfessionnelSante extends Utilisateur {
    private final String specialite;
    private final String numeroOrdre;
    private final List<Consultation> planning = new ArrayList<>();
    private String horairesDisponibilite;  // ex: "9h-12h, 14h-18h"

    public ProfessionnelSante(String loginID, String mdp, String role, String specialite, String numeroOrdre) {
        super(loginID, mdp, role);
        this.specialite = specialite;
        this.numeroOrdre = numeroOrdre;
        this.horairesDisponibilite = "9h-17h";  // Par défaut
    }

    public ProfessionnelSante(String loginID, String mdp, String role, String nom, String prenom, String specialite, String numeroOrdre) {
        super(loginID, mdp, role, nom, prenom);
        this.specialite = specialite;
        this.numeroOrdre = numeroOrdre;
        this.horairesDisponibilite = "9h-17h";
    }

    /**
     * Crée un patient (ici stub, la logique réelle se fait dans PatientService)
     */
    public boolean creerPatient(Patient p){ 
        return true; 
    }

    /**
     * Ajoute une consultation au planning
     */
    public void ajouterConsultation(Consultation c){
        planning.add(c);
    }

    /**
     * Annule une consultation (marque comme annulée au lieu de supprimer)
     */
    public boolean annulerConsultation(int idConsultation){
        for (Consultation c : planning) {
            if (c.getIdConsultation() == idConsultation) {
                c.setStatut("annulée");
                return true;
            }
        }
        return false;
    }

    /**
     * Récupère le planning complet
     */
    public List<Consultation> getPlanning(){ 
        return planning; 
    }

    /**
     * Récupère le nombre de consultations
     */
    public int getNombreConsultations(){
        return planning.size();
    }

    /**
     * Récupère les consultations effectuées
     */
    public List<Consultation> getConsultationsEffectuees(){
        List<Consultation> effectuees = new ArrayList<>();
        for(Consultation c : planning){
            if("effectuée".equalsIgnoreCase(c.getStatut())){
                effectuees.add(c);
            }
        }
        return effectuees;
    }

    /**
     * Vérification de disponibilité avec gestion des chevauchements
     */
    public boolean estDisponiblePour(Consultation nouvelleConsultation){
        LocalDateTime debut1 = nouvelleConsultation.getDateHeure();
        LocalDateTime fin1 = nouvelleConsultation.getFinConsultation();

        for (Consultation existante : planning) {
            // Ignorer les consultations annulées
            if ("annulée".equalsIgnoreCase(existante.getStatut())) continue;

            LocalDateTime debut2 = existante.getDateHeure();
            LocalDateTime fin2 = existante.getFinConsultation();

            // Test de chevauchement: (StartA < EndB) and (EndA > StartB)
            if (debut1.isBefore(fin2) && fin1.isAfter(debut2)) {
                return false; // Conflit trouvé
            }
        }
        return true;
    }

    // Getters
    public String getSpecialite(){ 
        return specialite; 
    }

    public String getNumeroOrdre(){
        return numeroOrdre;
    }

    public String getHorairesDisponibilite(){
        return horairesDisponibilite;
    }

    // Setters
    public void setHorairesDisponibilite(String horaires){
        this.horairesDisponibilite = horaires;
    }

    /**
     * Ajoute un antécédent au dossier d'un patient
     */
    public void ajouterAntecedentPatient(Patient patient, Antecedent antecedent){
        if(patient != null && patient.getDossierMedical() != null){
            patient.getDossierMedical().ajouterAntecedent(antecedent);
        }
    }

    @Override
    public String toString(){
        return String.format("[%s] Dr. %s %s - %s (Ordre: %s) - Planning: %d consultations",
                loginID, nom, prenom, specialite, numeroOrdre, planning.size());
    }
}