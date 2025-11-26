package com.medipass.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.medipass.model.Consultation;
import com.medipass.model.Patient;
import com.medipass.user.ProfessionnelSante;

/**
 * Service de gestion des consultations.
 * Permet de programmer, annuler et suivre les consultations.
 */
public class ConsultationService {
    private final List<Consultation> consultations = new ArrayList<>();

    /**
     * Programme une nouvelle consultation
     */
    public boolean programmerConsultation(LocalDateTime dateHeure, String motif, 
                                        ProfessionnelSante professionnel, Patient patient) {
        if (dateHeure == null || motif == null || professionnel == null || patient == null) {
            return false;
        }

        // Vérifier la disponibilité du professionnel
        if (!professionnel.estDisponiblePour(new Consultation(dateHeure, motif, professionnel, patient))) {
            return false;
        }

        Consultation consultation = new Consultation(dateHeure, motif, professionnel, patient);
        professionnel.ajouterConsultation(consultation);
        patient.getDossierMedical().ajouterConsultation(consultation);
        consultations.add(consultation);
        return true;
    }

    /**
     * Ajoute une consultation existante (chargée depuis la BDD/Fichier)
     */
    public void ajouterConsultationExistante(Consultation c) {
        if (c != null) {
            consultations.add(c);
        }
    }

    /**
     * Annule une consultation
     */
    public boolean annulerConsultation(int consultationId) {
        Consultation consultation = findConsultationById(consultationId);
        if (consultation == null) {
            return false;
        }

        // Retirer de la liste globale
        consultations.remove(consultation);

        // Retirer du planning du professionnel
        consultation.getProfessionnel().annulerConsultation(consultationId);

        return true;
    }

    /**
     * Recherche une consultation par ID
     */
    public Consultation findConsultationById(int id) {
        return consultations.stream()
                .filter(c -> c.getIdConsultation() == id)
                .findFirst().orElse(null);
    }

    /**
     * Récupère toutes les consultations
     */
    public List<Consultation> getConsultations() {
        return new ArrayList<>(consultations);
    }

    /**
     * Récupère les consultations d'un patient
     */
    public List<Consultation> getConsultationsPatient(Patient patient) {
        List<Consultation> result = new ArrayList<>();
        for (Consultation c : consultations) {
            if (c.getPatient().getId() == patient.getId()) {
                result.add(c);
            }
        }
        return result;
    }

    /**
     * Récupère les consultations d'un professionnel
     */
    public List<Consultation> getConsultationsProfessionnel(ProfessionnelSante professionnel) {
        return new ArrayList<>(professionnel.getPlanning());
    }

    /**
     * Ajoute des observations à une consultation
     */
    public boolean ajouterObservations(int consultationId, String observations) {
        Consultation consultation = findConsultationById(consultationId);
        if (consultation == null) {
            return false;
        }
        consultation.setObservations(observations);
        return true;
    }

    /**
     * Ajoute un diagnostic à une consultation
     */
    public boolean ajouterDiagnostic(int consultationId, String diagnostic) {
        Consultation consultation = findConsultationById(consultationId);
        if (consultation == null) {
            return false;
        }
        consultation.setDiagnostic(diagnostic);
        return true;
    }

    /**
     * Marque une consultation comme effectuée
     */
    public boolean marquerEffectuee(int consultationId) {
        Consultation consultation = findConsultationById(consultationId);
        if (consultation == null) {
            return false;
        }
        consultation.setStatut("effectuée");
        return true;
    }

    /**
     * Obtient le nombre total de consultations
     */
    public int getNombreConsultations() {
        return consultations.size();
    }

    /**
     * Affiche les détails d'une consultation
     */
    public String afficherConsultation(int consultationId) {
        Consultation consultation = findConsultationById(consultationId);
        if (consultation == null) {
            return "Consultation non trouvée";
        }
        return consultation.toString();
    }

    /**
     * Affiche les consultations d'un patient
     */
    public String afficherHistoriquePatient(Patient patient) {
        List<Consultation> consultationsPatient = getConsultationsPatient(patient);
        StringBuilder sb = new StringBuilder();
        sb.append("=== HISTORIQUE CONSULTATIONS DE ").append(patient.getNom()).append(" ").append(patient.getPrenom()).append(" ===\n");
        if (consultationsPatient.isEmpty()) {
            sb.append("Aucune consultation enregistrée\n");
        } else {
            for (Consultation c : consultationsPatient) {
                sb.append(c).append("\n");
            }
        }
        return sb.toString();
    }
}
