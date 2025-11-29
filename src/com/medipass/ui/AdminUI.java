package com.medipass.ui;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

import com.medipass.model.*;
import com.medipass.service.*;
import com.medipass.user.Administrateur;

/**
 * Interface utilisateur pour les administrateurs
 * L'administrateur ne peut PAS accéder aux données médicales (antécédents, diagnostics)
 */
public class AdminUI implements MenuInterface {

    private final Scanner sc;
    private final Administrateur admin;
    private final PatientService patientService;
    private final ConsultationService consultationService;
    private final AdministrateurService adminService;
    private final StatistiquesService statsService;
    private final DataService dataService;
    private final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public AdminUI(Scanner sc,
                   Administrateur admin,
                   PatientService patientService,
                   ConsultationService consultationService,
                   AdministrateurService adminService,
                   StatistiquesService statsService,
                   DataService dataService) {
        this.sc = sc;
        this.admin = admin;
        this.patientService = patientService;
        this.consultationService = consultationService;
        this.adminService = adminService;
        this.statsService = statsService;
        this.dataService = dataService;
    }

    @Override
    public void afficherMenu() {
        boolean continuer = true;
        while (continuer) {
            System.out.println("\n╔═══════════════════════════════════════╗");
            System.out.println("║  MENU ADMINISTRATEUR                  ║");
            System.out.println("╠═══════════════════════════════════════╣");
            System.out.println("║ 1) Gestion des utilisateurs           ║");
            System.out.println("║ 2) Gestion des patients               ║");
            System.out.println("║ 3) Statistiques du système            ║");
            System.out.println("║ 4) Sauvegarder les données            ║");
            System.out.println("║ 0) Se déconnecter                     ║");
            System.out.println("╚═══════════════════════════════════════╝");
            System.out.print("Votre choix: ");
            String choix = sc.nextLine().trim();

            switch (choix) {
                case "1" -> menuGestionUtilisateurs();
                case "2" -> menuGestionPatients();
                case "3" -> menuStatistiques();
                case "4" -> sauvegarderDonnees();
                case "0" -> continuer = false;
                default -> System.out.println("❌ Choix invalide");
            }
        }
    }

    /* ===================== UTILISATEURS ===================== */

    private void menuGestionUtilisateurs() {
        boolean continuer = true;
        while (continuer) {
            System.out.println("\n╔═══════════════════════════════════════╗");
            System.out.println("║  GESTION DES UTILISATEURS             ║");
            System.out.println("╠═══════════════════════════════════════╣");
            System.out.println("║ 1) Lister les professionnels          ║");
            System.out.println("║ 2) Afficher un utilisateur            ║");
            System.out.println("║ 3) Modifier contact utilisateur       ║");
            System.out.println("║ 4) Activer/Désactiver compte          ║");
            System.out.println("║ 5) Créer un professionnel             ║");
            System.out.println("║ 6) Supprimer un utilisateur           ║");
            System.out.println("║ 0) Retour                             ║");
            System.out.println("╚═══════════════════════════════════════╝");
            System.out.print("Votre choix: ");
            String choix = sc.nextLine().trim();

            switch (choix) {
                case "1" -> System.out.println(adminService.afficherProfessionnels());
                case "2" -> afficherUtilisateur();
                case "3" -> modifierContactUtilisateur();
                case "4" -> activerDesactiverCompte();
                case "5" -> creerProfessionnel();
                case "6" -> supprimerUtilisateur();
                case "0" -> continuer = false;
                default -> System.out.println("❌ Choix invalide");
            }
        }
    }

    private void afficherUtilisateur() {
        String login = lireChaine("Login de l'utilisateur: ");
        System.out.println(adminService.afficherUtilisateur(login));
    }

    private void modifierContactUtilisateur() {
        String login = lireChaine("Login de l'utilisateur: ");
        String email = lireChaine("Nouvel email (ou vide pour ne pas changer): ");
        String telephone = lireChaine("Nouveau téléphone (ou vide pour ne pas changer): ");

        boolean success = adminService.modifierContactUtilisateur(
                login,
                email.isEmpty() ? null : email,
                telephone.isEmpty() ? null : telephone
        );

        if (success) {
            System.out.println("✓ Contact modifié avec succès");
            sauvegarderDonnees();
        } else {
            System.out.println("❌ Utilisateur non trouvé");
        }
    }

    private void activerDesactiverCompte() {
        String login = lireChaine("Login de l'utilisateur: ");
        String action = lireChaine(
                "1) Activer compte utilisateur\n0) Désactiver compte utilisateur\nAction:").toLowerCase();

        boolean success = false;
        if ("1".equals(action)) {
            success = adminService.activerCompte(login);
        } else if ("0".equals(action)) {
            success = adminService.desactiverCompte(login);
        }

        if (success) {
            System.out.println("✓ Compte " + (action.equals("0") ? "désactivé" : "activé") + " avec succès");
            sauvegarderDonnees();
        } else {
            System.out.println("❌ Opération échouée");
        }
    }

    /* ===================== PATIENTS ===================== */

    private void menuGestionPatients() {
        boolean continuer = true;
        while (continuer) {
            System.out.println("\n╔═══════════════════════════════════════╗");
            System.out.println("║  GESTION DES PATIENTS                 ║");
            System.out.println("║  (Données administratives uniquement) ║");
            System.out.println("╠═══════════════════════════════════════╣");
            System.out.println("║ 1) Créer un patient                   ║");
            System.out.println("║ 2) Lister les patients                ║");
            System.out.println("║ 3) Consulter infos administratives    ║");
            System.out.println("║ 4) Modifier infos administratives     ║");
            System.out.println("║ 0) Retour                             ║");
            System.out.println("╚═══════════════════════════════════════╝");
            System.out.println("⚠️  Note: Les données médicales sont accessibles");
            System.out.println("    uniquement par les professionnels de santé");
            System.out.print("\nVotre choix: ");
            String choix = sc.nextLine().trim();

            switch (choix) {
                case "1" -> creerPatient();
                case "2" -> listerPatients();
                case "3" -> consulterInfosAdministratives();
                case "4" -> modifierPatient();
                case "0" -> continuer = false;
                default -> System.out.println("❌ Choix invalide");
            }
        }
    }

    private void creerPatient() {
        System.out.println("\n--- Création d'un patient ---");
        int id = lireEntier("ID: ");
        String nom = lireChaine("Nom: ");
        String prenom = lireChaine("Prénom: ");

        Patient patient = new Patient(id, nom, prenom);
        patient.setNumeroSecuriteSociale(lireChaine("Numéro de Sécurité Sociale: "));
        patient.setGroupeSanguin(lireChaine("Groupe sanguin: "));

        if (patientService.creerPatient(patient)) {
            System.out.println("✓ Patient créé avec succès. Dossier ID: "
                    + patient.getDossierMedical().getIdDossier());
            sauvegarderDonnees();
        } else {
            System.out.println("❌ Erreur lors de la création (ID peut-être déjà utilisé)");
        }
    }

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

    private void consulterInfosAdministratives() {
        int id = lireEntier("ID du patient: ");
        Patient patient = patientService.findPatientById(id);

        if (patient == null) {
            System.out.println("❌ Patient non trouvé");
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("\n=== INFORMATIONS ADMINISTRATIVES ===\n");
        sb.append("ID: ").append(patient.getId()).append("\n");
        sb.append("Nom: ").append(patient.getNom()).append("\n");
        sb.append("Prénom: ").append(patient.getPrenom()).append("\n");
        sb.append("Numéro SS: ").append(patient.getNumeroSecuriteSociale()).append("\n");
        sb.append("Groupe sanguin: ").append(patient.getGroupeSanguin()).append("\n");
        sb.append("Dossier médical ID: ").append(patient.getDossierMedical().getIdDossier()).append("\n");
        sb.append("\n⚠️  Les données médicales (antécédents, consultations)\n");
        sb.append("   sont accessibles uniquement par les professionnels de santé.\n");

        System.out.println(sb);
    }

    private void modifierPatient() {
        int id = lireEntier("ID du patient: ");
        String nom = lireChaine("Nouveau nom (ou vide): ");
        String prenom = lireChaine("Nouveau prénom (ou vide): ");
        String groupe = lireChaine("Nouveau groupe sanguin (ou vide): ");

        if (patientService.modifierPatient(
                id,
                nom.isEmpty() ? null : nom,
                prenom.isEmpty() ? null : prenom,
                null,
                groupe.isEmpty() ? null : groupe)) {
            System.out.println("✓ Patient modifié");
            sauvegarderDonnees();
        } else {
            System.out.println("❌ Patient non trouvé");
        }
    }

    /* ===================== STATISTIQUES & PLANNING ===================== */

    private void menuStatistiques() {
        System.out.println("\n╔═══════════════════════════════════════╗");
        System.out.println("║  STATISTIQUES                         ║");
        System.out.println("╠═══════════════════════════════════════╣");
        System.out.println("║ 1) Statistiques générales             ║");
        System.out.println("║ 2) Consultations par période          ║");
        System.out.println("║ 3) Planning d'un professionnel        ║");
        System.out.println("║ 0) Retour                             ║");
        System.out.println("╚═══════════════════════════════════════╝");
        System.out.print("Votre choix: ");
        String choix = sc.nextLine().trim();

        switch (choix) {
            case "1" -> afficherStatistiquesGenerales();
            case "2" -> afficherConsultationsParPeriode();
            case "3" -> afficherPlanningProfessionnel();
            case "0" -> { }
            default -> System.out.println("❌ Choix invalide");
        }
    }

    private void afficherStatistiquesGenerales() {
        System.out.println(statsService.afficherStatistiques(
                patientService.getNombrePatients(),
                adminService.getNombreProfessionnels(),
                consultationService.getNombreConsultations(),
                consultationService.getConsultations(),
                adminService.getProfessionnels()
        ));
    }

    private void afficherConsultationsParPeriode() {
        System.out.print("Date de début (YYYY-MM-DD): ");
        LocalDate debut = parseDate(sc.nextLine().trim());
        System.out.print("Date de fin (YYYY-MM-DD): ");
        LocalDate fin = parseDate(sc.nextLine().trim());

        if (debut == null || fin == null) {
            System.out.println("❌ Dates invalides");
            return;
        }

        List<Consultation> consultationsPeriode = consultationService.getConsultationsParPeriode(
                debut.atStartOfDay(), fin.atTime(23, 59));

        System.out.println("\n=== CONSULTATIONS DU " + debut + " AU " + fin + " ===");
        System.out.println("Nombre total : " + consultationsPeriode.size());

        Map<String, Long> parStatut = consultationsPeriode.stream()
                .collect(Collectors.groupingBy(Consultation::getStatut, Collectors.counting()));

        System.out.println("\nPar statut :");
        parStatut.forEach((statut, count) ->
                System.out.println("  - " + statut + " : " + count));

        Map<String, Long> parPro = consultationsPeriode.stream()
                .collect(Collectors.groupingBy(
                        c -> c.getProfessionnel().getNom() + " " + c.getProfessionnel().getPrenom(),
                        Collectors.counting()));

        System.out.println("\nPar professionnel :");
        parPro.forEach((pro, count) ->
                System.out.println("  - " + pro + " : " + count));
    }

    private void afficherPlanningProfessionnel() {
        String login = lireChaine("Login du professionnel: ");
        com.medipass.user.ProfessionnelSante pro = adminService.findProfessionnel(login);

        if (pro == null) {
            System.out.println("❌ Professionnel non trouvé");
            return;
        }

        System.out.print("Date de début (YYYY-MM-DD) [Entrée pour cette semaine]: ");
        String input = sc.nextLine().trim();
        LocalDate debut = input.isEmpty()
                ? LocalDate.now().with(java.time.DayOfWeek.MONDAY)
                : parseDate(input);

        if (debut != null) {
            System.out.println(consultationService.afficherPlanningSemaine(pro, debut));
        }
    }

    /* ===================== COMPTE / SAUVEGARDE ===================== */

    private void creerProfessionnel() {
        System.out.println("\n--- Création d'un professionnel de santé ---");
        String login = lireChaine("Login: ");
        String mdp = lireChaine("Mot de passe: ");
        String nom = lireChaine("Nom: ");
        String prenom = lireChaine("Prénom: ");
        String specialite = lireChaine("Spécialité: ");

        com.medipass.user.ProfessionnelSante pro =
                new com.medipass.user.ProfessionnelSante(
                        login, mdp, "PRO", nom, prenom, specialite,
                        "NUM" + System.currentTimeMillis() % 10000);

        if (adminService.creerCompte(pro)) {
            System.out.println("✓ Professionnel créé. Vous pouvez maintenant vous connecter.");
            sauvegarderDonnees();
        } else {
            System.out.println("❌ Login déjà existant");
        }
    }

    private void supprimerUtilisateur() {
        System.out.println("\n--- Suppression d'un utilisateur ---");
        String login = lireChaine("Login de l'utilisateur à supprimer: ");

        // Vérifier que l'utilisateur existe
        if (adminService.findUtilisateur(login) == null) {
            System.out.println("❌ Utilisateur introuvable.");
            return;
        }

        // Empêcher l'admin de se supprimer lui-même
        if (login.equals(admin.getLoginID())) {
            System.out.println("❌ Vous ne pouvez pas supprimer votre propre compte.");
            return;
        }

        System.out.println("⚠️  ATTENTION : Cette action est irréversible !");
        String password = lireChaine("Confirmez avec votre mot de passe administrateur: ");

        if (admin.seConnecter(admin.getLoginID(), password)) {
            if (adminService.supprimerCompte(login)) {
                System.out.println("✓ Utilisateur supprimé avec succès.");
                sauvegarderDonnees();
            } else {
                System.out.println("❌ Erreur lors de la suppression.");
            }
        } else {
            System.out.println("❌ Mot de passe incorrect. Suppression annulée.");
        }
    }

    private void sauvegarderDonnees() {
        dataService.savePatients(patientService.getPatients());
        dataService.saveProfessionnels(adminService.getProfessionnels());
        dataService.saveConsultations(consultationService.getConsultations());
        dataService.saveAntecedents(patientService.getPatients());
        System.out.println("(Données sauvegardées)");
    }

    /* ===================== UTILITAIRES ===================== */

    private String lireChaine(String prompt) {
        System.out.print(prompt);
        return sc.nextLine().trim();
    }

    private int lireEntier(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                String input = sc.nextLine().trim();
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("❌ Veuillez entrer un nombre entier valide.");
            }
        }
    }

    private LocalDate parseDate(String input) {
        try {
            return LocalDate.parse(input);
        } catch (Exception e) {
            System.out.println("❌ Format invalide (utilisez YYYY-MM-DD)");
            return null;
        }
    }
}
