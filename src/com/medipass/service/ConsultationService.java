package com.medipass.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import com.medipass.model.Consultation;
import com.medipass.model.Patient;
import com.medipass.user.ProfessionnelSante;

/**
 * Service de gestion des consultations.
 * Permet de programmer, annuler et suivre les consultations avec gestion des périodes.
 */
public class ConsultationService {
    private final List<Consultation> consultations = new ArrayList<>();

    /**
     * Programme une nouvelle consultation avec validations complètes
     */
    public boolean programmerConsultation(LocalDateTime dateHeure, String motif, 
                                        ProfessionnelSante professionnel, Patient patient) {
        // Validations de base
        if (dateHeure == null || motif == null || professionnel == null || patient == null) {
            System.err.println("❌ Paramètres invalides");
            return false;
        }

        // Vérifier que ce n'est pas dans le passé
        if (dateHeure.isBefore(LocalDateTime.now())) {
            System.err.println("❌ Impossible de programmer une consultation dans le passé");
            return false;
        }

        // Vérifier que le motif n'est pas vide
        if (motif.trim().isEmpty()) {
            System.err.println("❌ Le motif ne peut pas être vide");
            return false;
        }

        // Créer une consultation temporaire pour les tests
        Consultation nouvelleConsultation = new Consultation(dateHeure, motif, professionnel, patient);

        // Vérifier la disponibilité du professionnel
        if (!professionnel.estDisponiblePour(nouvelleConsultation)) {
            System.err.println("❌ Le professionnel n'est pas disponible à cette heure");
            return false;
        }

        // Vérifier la disponibilité du patient
        if (!patientEstDisponible(patient, nouvelleConsultation)) {
            System.err.println("❌ Le patient a déjà une consultation à cette heure");
            return false;
        }

        // Tout est OK, enregistrer la consultation
        professionnel.ajouterConsultation(nouvelleConsultation);
        patient.getDossierMedical().ajouterConsultation(nouvelleConsultation);
        consultations.add(nouvelleConsultation);
        
        return true;
    }

    /**
     * Vérifie si un patient est disponible pour une consultation
     */
    private boolean patientEstDisponible(Patient patient, Consultation nouvelleConsultation) {
        LocalDateTime debut1 = nouvelleConsultation.getDateHeure();
        LocalDateTime fin1 = nouvelleConsultation.getFinConsultation();

        for (Consultation existante : patient.getDossierMedical().getConsultations()) {
            // Ignorer les consultations annulées
            if ("annulée".equalsIgnoreCase(existante.getStatut())) {
                continue;
            }

            LocalDateTime debut2 = existante.getDateHeure();
            LocalDateTime fin2 = existante.getFinConsultation();

            // Test de chevauchement: (StartA < EndB) and (EndA > StartB)
            if (debut1.isBefore(fin2) && fin1.isAfter(debut2)) {
                return false; // Conflit trouvé
            }
        }
        return true;
    }

    /**
     * Ajoute une consultation existante (chargée depuis la BDD/Fichier)
     */
    public void ajouterConsultationExistante(Consultation c) {
        if (c == null) {
            return;
        }
        
        // Vérifier qu'elle n'existe pas déjà
        if (findConsultationById(c.getIdConsultation()) != null) {
            System.err.println("⚠️ Consultation " + c.getIdConsultation() + " existe déjà, ignorée");
            return;
        }
        
        consultations.add(c);
    }

    /**
     * Annule une consultation (la marque comme annulée sans la supprimer)
     */
    public boolean annulerConsultation(int consultationId) {
        Consultation consultation = findConsultationById(consultationId);
        if (consultation == null) {
            return false;
        }

        // Marquer comme annulée au lieu de supprimer (historique médical)
        consultation.setStatut("annulée");
        
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
        return consultations.stream()
                .filter(c -> c.getPatient().getId() == patient.getId())
                .collect(Collectors.toList());
    }

    /**
     * Récupère les consultations d'un professionnel
     */
    public List<Consultation> getConsultationsProfessionnel(ProfessionnelSante professionnel) {
        return new ArrayList<>(professionnel.getPlanning());
    }

    /**
     * Récupère les consultations entre deux dates
     */
    public List<Consultation> getConsultationsParPeriode(LocalDateTime debut, LocalDateTime fin) {
        return consultations.stream()
                .filter(c -> !c.getDateHeure().isBefore(debut) && c.getDateHeure().isBefore(fin))
                .collect(Collectors.toList());
    }

    /**
     * Récupère les consultations selon leur statut
     */
    public List<Consultation> getConsultationsParStatut(String statut) {
        return consultations.stream()
                .filter(c -> c.getStatut().equalsIgnoreCase(statut))
                .collect(Collectors.toList());
    }

    /**
     * Récupère les prochaines consultations d'un professionnel (non effectuées)
     */
    public List<Consultation> getProchainesConsultations(ProfessionnelSante professionnel) {
        LocalDateTime maintenant = LocalDateTime.now();
        return consultations.stream()
                .filter(c -> c.getProfessionnel().getLoginID().equals(professionnel.getLoginID()))
                .filter(c -> c.getDateHeure().isAfter(maintenant))
                .filter(c -> !"annulée".equalsIgnoreCase(c.getStatut()))
                .sorted((c1, c2) -> c1.getDateHeure().compareTo(c2.getDateHeure()))
                .collect(Collectors.toList());
    }

    /**
     * Affiche le planning d'un professionnel pour une période donnée
     */
    public String afficherPlanningPeriode(ProfessionnelSante professionnel, 
                                         LocalDateTime debut, LocalDateTime fin) {
        List<Consultation> consultationsPeriode = consultations.stream()
            .filter(c -> c.getProfessionnel().getLoginID().equals(professionnel.getLoginID()))
            .filter(c -> !c.getDateHeure().isBefore(debut) && c.getDateHeure().isBefore(fin))
            .filter(c -> !"annulée".equalsIgnoreCase(c.getStatut()))
            .sorted((c1, c2) -> c1.getDateHeure().compareTo(c2.getDateHeure()))
            .collect(Collectors.toList());

        StringBuilder sb = new StringBuilder();
        sb.append("=== PLANNING DE ").append(professionnel.getNom()).append(" ")
          .append(professionnel.getPrenom()).append(" ===\n");
        sb.append("Période : ").append(debut.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
          .append(" au ").append(fin.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))).append("\n\n");

        if (consultationsPeriode.isEmpty()) {
            sb.append("Aucune consultation programmée sur cette période\n");
        } else {
            // Grouper par jour
            Map<LocalDate, List<Consultation>> parJour = consultationsPeriode.stream()
                .collect(Collectors.groupingBy(c -> c.getDateHeure().toLocalDate()));

            // Afficher jour par jour
            parJour.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> {
                    LocalDate jour = entry.getKey();
                    List<Consultation> consultationsJour = entry.getValue();
                    
                    sb.append("--- ").append(jour.format(DateTimeFormatter.ofPattern("EEEE dd MMMM yyyy", Locale.FRENCH)))
                      .append(" ---\n");
                    
                    consultationsJour.forEach(c -> {
                        sb.append(String.format("  %s - %s : %s %s (%s)\n",
                            c.getDateHeure().format(DateTimeFormatter.ofPattern("HH:mm")),
                            c.getFinConsultation().format(DateTimeFormatter.ofPattern("HH:mm")),
                            c.getPatient().getNom(),
                            c.getPatient().getPrenom(),
                            c.getMotif()
                        ));
                    });
                    sb.append("\n");
                });
        }
        
        return sb.toString();
    }

    /**
     * Affiche le planning d'un professionnel pour une semaine
     */
    public String afficherPlanningSemaine(ProfessionnelSante professionnel, LocalDate dateDebut) {
        LocalDateTime debut = dateDebut.atStartOfDay();
        LocalDateTime fin = debut.plusWeeks(1);
        return afficherPlanningPeriode(professionnel, debut, fin);
    }

    /**
     * Affiche le planning d'un professionnel pour un mois
     */
    public String afficherPlanningMois(ProfessionnelSante professionnel, int annee, int mois) {
        LocalDateTime debut = LocalDateTime.of(annee, mois, 1, 0, 0);
        LocalDateTime fin = debut.plusMonths(1);
        return afficherPlanningPeriode(professionnel, debut, fin);
    }

    /**
     * Affiche le planning d'un professionnel pour une journée
     */
    public String afficherPlanningJour(ProfessionnelSante professionnel, LocalDate date) {
        LocalDateTime debut = date.atStartOfDay();
        LocalDateTime fin = debut.plusDays(1);
        return afficherPlanningPeriode(professionnel, debut, fin);
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
     * Obtient le nombre de consultations annulées
     */
    public int getNombreConsultationsAnnulees() {
        return (int) consultations.stream()
                .filter(c -> "annulée".equalsIgnoreCase(c.getStatut()))
                .count();
    }

    /**
     * Calcule le taux de consultations effectuées
     */
    public double getTauxConsultationsEffectuees() {
        if (consultations.isEmpty()) return 0.0;
        
        long effectuees = consultations.stream()
                .filter(c -> "effectuée".equalsIgnoreCase(c.getStatut()))
                .count();
        
        return (double) effectuees / consultations.size() * 100;
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
        sb.append("=== HISTORIQUE CONSULTATIONS DE ")
          .append(patient.getNom()).append(" ")
          .append(patient.getPrenom()).append(" ===\n");
        
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