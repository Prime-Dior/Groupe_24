package com.medipass.service;

import java.io.*;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.medipass.model.*;
import com.medipass.user.*;

/**
 * Service de persistance des données en fichiers CSV
 */
public class DataService {

    private static final String PATIENTS_FILE = "patients.csv";
    private static final String PROS_FILE = "pros.csv";
    private static final String CONSULTATIONS_FILE = "consultations.csv";
    private static final String ANTECEDENTS_FILE = "antecedents.csv";

    // ========== PATIENTS ==========

    public void savePatients(List<Patient> patients) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(PATIENTS_FILE))) {
            writer.println("id;nom;prenom;numeroSecuriteSociale;groupeSanguin");
            for (Patient p : patients) {
                writer.printf("%d;%s;%s;%s;%s\n",
                        p.getId(),
                        p.getNom(),
                        p.getPrenom(),
                        p.getNumeroSecuriteSociale() != null ? p.getNumeroSecuriteSociale() : "",
                        p.getGroupeSanguin() != null ? p.getGroupeSanguin() : ""
                );
            }
        } catch (IOException e) {
            System.err.println("Erreur sauvegarde patients: " + e.getMessage());
        }
    }

    public List<Patient> loadPatients() {
        List<Patient> patients = new ArrayList<>();
        File file = new File(PATIENTS_FILE);
        if (!file.exists()) {
            return patients;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean isHeader = true;
            while ((line = reader.readLine()) != null) {
                if (isHeader) {
                    isHeader = false;
                    continue;
                }
                String[] parts = line.split(";");
                if (parts.length >= 3) {
                    try {
                        int id = Integer.parseInt(parts[0]);
                        Patient p = new Patient(id, parts[1], parts[2]);
                        if (parts.length > 3 && !parts[3].isEmpty()) {
                            p.setNumeroSecuriteSociale(parts[3]);
                        }
                        if (parts.length > 4 && !parts[4].isEmpty()) {
                            p.setGroupeSanguin(parts[4]);
                        }
                        patients.add(p);
                    } catch (NumberFormatException e) {
                        System.err.println("Erreur parsing patient: " + e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur chargement patients: " + e.getMessage());
        }
        return patients;
    }

    // ========== PROFESSIONNELS ==========

    public void saveProfessionnels(List<ProfessionnelSante> pros) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(PROS_FILE))) {
            writer.println("login;password;nom;prenom;specialite;numeroOrdre;horairesDisponibilite");
            for (ProfessionnelSante p : pros) {
                writer.printf("%s;%s;%s;%s;%s;%s;%s\n",
                        p.getLoginID(),
                        p.getPassword(),
                        p.getNom(),
                        p.getPrenom(),
                        p.getSpecialite(),
                        p.getNumeroOrdre(),
                        p.getHorairesDisponibilite()
                );
            }
        } catch (IOException e) {
            System.err.println("Erreur sauvegarde professionnels: " + e.getMessage());
        }
    }

    public List<ProfessionnelSante> loadProfessionnels() {
        List<ProfessionnelSante> pros = new ArrayList<>();
        File file = new File(PROS_FILE);
        if (!file.exists()) {
            return pros;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean isHeader = true;
            while ((line = reader.readLine()) != null) {
                if (isHeader) {
                    isHeader = false;
                    continue;
                }
                String[] parts = line.split(";");
                if (parts.length >= 6) {
                    try {
                        ProfessionnelSante p = new ProfessionnelSante(
                                parts[0], parts[1], "PRO", parts[2], parts[3], parts[4], parts[5]
                        );
                        if (parts.length > 6 && !parts[6].isEmpty()) {
                            p.setHorairesDisponibilite(parts[6]);
                        }
                        pros.add(p);
                    } catch (Exception e) {
                        System.err.println("Erreur parsing professionnel: " + e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur chargement professionnels: " + e.getMessage());
        }
        return pros;
    }

    // ========== CONSULTATIONS ==========

    public void saveConsultations(List<Consultation> consultations) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(CONSULTATIONS_FILE))) {
            writer.println("dateHeure;motif;professionnelLogin;patientId;dureeMinutes;statut;observations;diagnostic");
            for (Consultation c : consultations) {
                writer.printf("%s;%s;%s;%d;%d;%s;%s;%s\n",
                        c.getDateHeure().toString(),
                        c.getMotif(),
                        c.getProfessionnel().getLoginID(),
                        c.getPatient().getId(),
                        c.getDureeMinutes(),
                        c.getStatut(),
                        c.getObservations() != null ? c.getObservations().replace(";", ",") : "",
                        c.getDiagnostic() != null ? c.getDiagnostic().replace(";", ",") : ""
                );
            }
        } catch (IOException e) {
            System.err.println("Erreur sauvegarde consultations: " + e.getMessage());
        }
    }

    public List<Consultation> loadConsultations(List<Patient> patients, List<ProfessionnelSante> pros) {
        List<Consultation> consultations = new ArrayList<>();
        File file = new File(CONSULTATIONS_FILE);
        if (!file.exists()) {
            return consultations;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean isHeader = true;
            while ((line = reader.readLine()) != null) {
                if (isHeader) {
                    isHeader = false;
                    continue;
                }
                String[] parts = line.split(";");
                if (parts.length >= 4) {
                    try {
                        LocalDateTime date = LocalDateTime.parse(parts[0]);
                        String motif = parts[1];
                        String proLogin = parts[2];
                        int patientId = Integer.parseInt(parts[3]);

                        ProfessionnelSante pro = pros.stream()
                                .filter(p -> p.getLoginID().equals(proLogin))
                                .findFirst().orElse(null);

                        Patient patient = patients.stream()
                                .filter(p -> p.getId() == patientId)
                                .findFirst().orElse(null);

                        if (pro != null && patient != null) {
                            Consultation c = new Consultation(date, motif, pro, patient);
                            if (parts.length > 4 && !parts[4].isEmpty()) {
                                c.setDureeMinutes(Integer.parseInt(parts[4]));
                            }
                            if (parts.length > 5 && !parts[5].isEmpty()) {
                                c.setStatut(parts[5]);
                            }
                            if (parts.length > 6 && !parts[6].isEmpty()) {
                                c.setObservations(parts[6]);
                            }
                            if (parts.length > 7 && !parts[7].isEmpty()) {
                                c.setDiagnostic(parts[7]);
                            }

                            consultations.add(c);

                            // Re-link to objects
                            pro.ajouterConsultation(c);
                            patient.getDossierMedical().ajouterConsultation(c);
                        }
                    } catch (Exception e) {
                        System.err.println("Erreur parsing consultation: " + e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur chargement consultations: " + e.getMessage());
        }
        return consultations;
    }

    // ========== ANTÉCÉDENTS ==========

    /**
     * Sauvegarde les antécédents de tous les patients
     */
    public void saveAntecedents(List<Patient> patients) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(ANTECEDENTS_FILE))) {
            writer.println("patientId;type;description;date;gravite;actif");
            for (Patient p : patients) {
                for (Antecedent a : p.getDossierMedical().getAntecedents()) {
                    writer.printf("%d;%s;%s;%s;%s;%s\n",
                        p.getId(),
                        a.getType(),
                        a.getDescription() != null ? a.getDescription().replace(";", ",") : "",
                        a.getDate(),
                        a.getGravite(),
                        a.isActif()
                    );
                }
            }
        } catch (IOException e) {
            System.err.println("Erreur sauvegarde antécédents: " + e.getMessage());
        }
    }

    /**
     * Charge les antécédents depuis le fichier CSV
     */
    public void loadAntecedents(List<Patient> patients) {
        File file = new File(ANTECEDENTS_FILE);
        if (!file.exists()) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean isHeader = true;
            while ((line = reader.readLine()) != null) {
                if (isHeader) {
                    isHeader = false;
                    continue;
                }
                String[] parts = line.split(";");
                if (parts.length >= 5) {
                    try {
                        int patientId = Integer.parseInt(parts[0]);
                        Patient patient = patients.stream()
                            .filter(p -> p.getId() == patientId)
                            .findFirst().orElse(null);
                        
                        if (patient != null) {
                            Antecedent ant = new Antecedent(
                                parts[1], // type
                                parts[2], // description
                                LocalDate.parse(parts[3]), // date
                                parts[4], // gravité
                                parts.length > 5 ? Boolean.parseBoolean(parts[5]) : true // actif
                            );
                            patient.getDossierMedical().ajouterAntecedent(ant);
                        }
                    } catch (Exception e) {
                        System.err.println("Erreur parsing antécédent: " + e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur chargement antécédents: " + e.getMessage());
        }
    }
}