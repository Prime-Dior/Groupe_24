package com.medipass.service;

import java.util.ArrayList;
import java.util.List;

import com.medipass.model.Antecedent;
import com.medipass.model.Patient;

/**
 * Service de gestion des patients.
 * Permet de créer, rechercher, modifier et supprimer des patients.
 */
public class PatientService {
    private final List<Patient> patients = new ArrayList<>();

    /**
     * Crée un nouveau patient
     */
    public boolean creerPatient(Patient patient) {
        if (patient == null || findPatientById(patient.getId()) != null) {
            return false;
        }
        patients.add(patient);
        return true;
    }

    /**
     * Recherche un patient par ID
     */
    public Patient findPatientById(int id) {
        return patients.stream().filter(p -> p.getId() == id).findFirst().orElse(null);
    }

    /**
     * Recherche un patient par nom et prénom
     */
    public Patient findPatientByNomPrenom(String nom, String prenom) {
        return patients.stream()
                .filter(p -> p.getNom().equalsIgnoreCase(nom) && p.getPrenom().equalsIgnoreCase(prenom))
                .findFirst().orElse(null);
    }

    /**
     * Récupère tous les patients
     */
    public List<Patient> getPatients() {
        return new ArrayList<>(patients);
    }

    /**
     * Met à jour les informations d'un patient
     */
    public boolean modifierPatient(int id, String nom, String prenom, String numeroSS, String groupeSanguin) {
        Patient patient = findPatientById(id);
        if (patient == null) {
            return false;
        }
        if (nom != null && !nom.trim().isEmpty()) {
            patient.setNom(nom);
        }
        if (prenom != null && !prenom.trim().isEmpty()) {
            patient.setPrenom(prenom);
        }
        if (numeroSS != null) {
            patient.setNumeroSecuriteSociale(numeroSS);
        }
        if (groupeSanguin != null) {
            patient.setGroupeSanguin(groupeSanguin);
        }
        return true;
    }

    /**
     * Supprime un patient
     */
    public boolean supprimerPatient(int id) {
        return patients.removeIf(p -> p.getId() == id);
    }

    /**
     * Ajoute un antécédent au dossier médical d'un patient
     */
    public boolean ajouterAntecedentAuPatient(int patientId, Antecedent antecedent) {
        Patient patient = findPatientById(patientId);
        if (patient == null) {
            return false;
        }
        patient.getDossierMedical().ajouterAntecedent(antecedent);
        return true;
    }

    /**
     * Récupère les antécédents d'un patient
     */
    public List<Antecedent> getAntecedentsPatient(int patientId) {
        Patient patient = findPatientById(patientId);
        if (patient == null) {
            return new ArrayList<>();
        }
        return patient.getDossierMedical().getAntecedents();
    }

    /**
     * Recherche les patients par groupe sanguin
     */
    public List<Patient> findPatientsByGroupeSanguin(String groupe) {
        List<Patient> result = new ArrayList<>();
        for (Patient p : patients) {
            if (groupe.equalsIgnoreCase(p.getGroupeSanguin())) {
                result.add(p);
            }
        }
        return result;
    }

    /**
     * Obtient le nombre total de patients
     */
    public int getNombrePatients() {
        return patients.size();
    }

    /**
     * Affiche les informations d'un patient
     */
    public String afficherInfoPatient(int patientId) {
        Patient patient = findPatientById(patientId);
        if (patient == null) {
            return "Patient non trouvé";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("=== DOSSIER PATIENT ===\n");
        sb.append("ID: ").append(patient.getId()).append("\n");
        sb.append("Nom: ").append(patient.getNom()).append("\n");
        sb.append("Prénom: ").append(patient.getPrenom()).append("\n");
        sb.append("Numéro SS: ").append(patient.getNumeroSecuriteSociale()).append("\n");
        sb.append("Groupe sanguin: ").append(patient.getGroupeSanguin()).append("\n");
        sb.append("Dossier: ").append(patient.getDossierMedical().getIdDossier()).append("\n");
        sb.append("Antécédents: ").append(patient.getDossierMedical().getAntecedents().size()).append("\n");
        sb.append("Consultations: ").append(patient.getDossierMedical().getConsultations().size()).append("\n");
        return sb.toString();
    }
}
