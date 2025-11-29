package com.medipass.ui;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

import com.medipass.model.*;
import com.medipass.service.*;
import com.medipass.user.ProfessionnelSante;

/**
 * Interface utilisateur pour les professionnels de santé
 */
public class ProfessionelUI implements MenuInterface {

    private final Scanner sc;
    private final ProfessionnelSante professionnel;
    private final PatientService patientService;
    private final ConsultationService consultationService;
    private final DataService dataService;
    private final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public ProfessionelUI(Scanner sc,
                          ProfessionnelSante professionnel,
                          PatientService patientService,
                          ConsultationService consultationService,
                          DataService dataService) {
        this.sc = sc;
        this.professionnel = professionnel;
        this.patientService = patientService;
        this.consultationService = consultationService;
        this.dataService = dataService;
    }

    @Override
    public void afficherMenu() {
        boolean continuer = true;
        while (continuer) {
            System.out.println("\n╔═══════════════════════════════════════╗");
            System.out.println("║  MENU PROFESSIONNEL                   ║");
            System.out.println("║  Dr. " + professionnel.getNom() + " " + professionnel.getPrenom());
            System.out.println("╠═══════════════════════════════════════╣");
            System.out.println("║ 1) Consulter patients                 ║");
            System.out.println("║ 2) Programmer une consultation        ║");
            System.out.println("║ 3) Voir mon planning                  ║");
            System.out.println("║ 4) Clôturer une consultation          ║");
            System.out.println("║ 5) Gérer antécédents patient          ║");
            System.out.println("║ 6) Annuler une consultation           ║");
            System.out.println("║ 0) Se déconnecter                     ║");
            System.out.println("╚═══════════════════════════════════════╝");
            System.out.print("Votre choix: ");
            String choix = sc.nextLine().trim();

            switch (choix) {
                case "1" -> listerPatients();
                case "2" -> programmerConsultation();
                case "3" -> menuPlanning();
                case "4" -> clotureConsultation();
                case "5" -> menuAntecedents();
                case "6" -> annulerConsultation();
                case "0" -> continuer = false;
                default -> System.out.println("❌ Choix invalide");
            }
        }
    }

    /* ===================== PATIENTS ===================== */

    private void listerPatients() {
        System.out.println("\n=== LISTE DES PATIENTS ===");
        List<Patient> patients = patientService.getPatients();
        if (patients.isEmpty()) {
            System.out.println("Aucun patient enregistré");
        } else {
            for (Patient p : patients) {
                System.out.printf("[%d] %s %s%n", p.getId(), p.getNom(), p.getPrenom());
            }
        }
    }

    /* ===================== CONSULTATIONS ===================== */

    private void programmerConsultation() {
        System.out.println("\n--- Programmation d'une consultation ---");
        int patientId = lireEntier("ID du patient: ");

        Patient patient = patientService.findPatientById(patientId);
        if (patient == null) {
            System.out.println("❌ Patient non trouvé");
            return;
        }

        LocalDateTime dateHeure = lireDateTime("Date et heure (YYYY-MM-DD HH:MM): ");
        if (dateHeure == null) {
            return;
        }

        int duree = lireEntier("Durée en minutes (défaut 30): ");
        if (duree <= 0) {
            duree = 30;
        }

        String motif = lireChaine("Motif: ");

        if (consultationService.programmerConsultation(dateHeure, motif, professionnel, patient)) {
            List<Consultation> all = consultationService.getConsultations();
            if (!all.isEmpty()) {
                Consultation created = all.get(all.size() - 1);
                created.setDureeMinutes(duree);
            }
            System.out.println("✓ Consultation programmée");
            sauvegarderDonnees();
        } else {
            System.out.println("❌ Impossible de programmer (conflit horaire ou erreur)");
        }
    }

    private void clotureConsultation() {
        int id = lireEntier("ID de la consultation: ");
        String observations = lireChaine("Observations: ");
        String diagnostic = lireChaine("Diagnostic: ");

        boolean obsOk = consultationService.ajouterObservations(id, observations);
        boolean diagOk = consultationService.ajouterDiagnostic(id, diagnostic);
        boolean effectueOk = consultationService.marquerEffectuee(id);

        if (obsOk && diagOk && effectueOk) {
            System.out.println("✓ Consultation clôturée");
            sauvegarderDonnees();
        } else {
            System.out.println("❌ Consultation non trouvée");
        }
    }

    private void annulerConsultation() {
        int id = lireEntier("ID de la consultation à annuler: ");

        Consultation consultation = consultationService.findConsultationById(id);
        if (consultation == null) {
            System.out.println("❌ Consultation non trouvée");
            return;
        }

        if (!consultation.getProfessionnel().getLoginID().equals(professionnel.getLoginID())) {
            System.out.println("❌ Cette consultation ne vous appartient pas");
            return;
        }

        if (consultationService.annulerConsultation(id)) {
            System.out.println("✓ Consultation annulée");
            sauvegarderDonnees();
        } else {
            System.out.println("❌ Erreur lors de l'annulation");
        }
    }

    /* ===================== PLANNING ===================== */

    private void menuPlanning() {
        System.out.println("\n╔═══════════════════════════════════════╗");
        System.out.println("║  PLANNING DE " + professionnel.getNom() + " " + professionnel.getPrenom());
        System.out.println("╠═══════════════════════════════════════╣");
        System.out.println("║ 1) Planning complet                   ║");
        System.out.println("║ 2) Planning du jour                   ║");
        System.out.println("║ 3) Planning de la semaine             ║");
        System.out.println("║ 4) Planning du mois                   ║");
        System.out.println("║ 5) Planning personnalisé              ║");
        System.out.println("║ 0) Retour                             ║");
        System.out.println("╚═══════════════════════════════════════╝");
        System.out.print("Votre choix: ");
        String choix = sc.nextLine().trim();

        switch (choix) {
            case "1" -> afficherPlanningComplet();
            case "2" -> afficherPlanningJour();
            case "3" -> afficherPlanningSemaine();
