package com.medipass.app;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

import com.medipass.model.*;
import com.medipass.security.AuthentificationService;
import com.medipass.ui.*;
import com.medipass.user.*;
import com.medipass.service.*;

/**
 * Application MediPass - Système d'Information Médical
 * Point d'entrée de l'application
 * Gère l'authentification et dispatche vers les interfaces utilisateur appropriées
 */
public class Main {
    private static final Scanner sc = new Scanner(System.in);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    // Services
    private static final AuthentificationService auth = new AuthentificationService();
    private static final PatientService patientService = new PatientService();
    private static final ConsultationService consultationService = new ConsultationService();
    private static final AdministrateurService adminService = new AdministrateurService();
    private static final StatistiquesService statsService = new StatistiquesService();
    private static final DataService dataService = new DataService();

    public static void main(String[] args) {
        initializationSysteme();

        System.out.println("╔════════════════════════════════════╗");
        System.out.println("║        BIENVENUE À MEDIPASS        ║");
        System.out.println("║  Système d'Information Médical     ║");
        System.out.println("╚════════════════════════════════════╝");

        boolean running = true;
        while (running) {
            afficherMenuPrincipal();
            String choix = sc.nextLine().trim();

            switch (choix) {
                case "1" -> handleAuthentification();
                case "2" -> {
                    sauvegarderDonnees();
                    System.out.println("Au revoir!");
                    running = false;
                }
                default -> System.out.println("❌ Choix invalide. Veuillez réessayer.");
            }
        }
        sc.close();
    }

    private static void afficherMenuPrincipal() {
        System.out.println("\n╔════════════════════════════════╗");
        System.out.println("║     MENU PRINCIPAL             ║");
        System.out.println("╠════════════════════════════════╣");
        System.out.println("║ 1) Se connecter                ║");
        System.out.println("║ 2) Quitter                     ║");
        System.out.println("╚════════════════════════════════╝");
        System.out.print("Votre choix: ");
    }

    /**
     * Gère l'authentification et dispatche vers l'interface appropriée
     */
    private static void handleAuthentification() {
        String login = lireChaine("\nLogin: ");
        String mdp = lireChaine("Mot de passe: ");

        if (auth.login(login, mdp)) {
            Utilisateur u = auth.getCurrentUser();
            System.out.println("\n✓ Connexion réussie! Bienvenue " + u.getNom() + " " + u.getPrenom());

            // Dispatcher vers l'interface appropriée selon le type d'utilisateur
            switch (u) {
                case ProfessionnelSante pro -> {
                    MenuInterface menu = new ProfessionelUI(sc, pro, patientService, consultationService, dataService);
                    menu.afficherMenu();
                }
                case Administrateur admin -> {
                    MenuInterface menu = new AdminUI(sc, admin, patientService, consultationService, 
                        adminService, statsService, dataService);
                    menu.afficherMenu();
                }
                default -> System.out.println("Menu non disponible pour ce rôle.");
            }
            auth.logout();
        } else {
            System.out.println("❌ Identifiants incorrects ou compte inactif.");
        }
    }

    /**
     * Initialise le système avec données en mémoire ou données sauvegardées
     */
    private static void initializationSysteme() {
        // Essayer de charger les données
        List<Patient> patients = dataService.loadPatients();
        List<ProfessionnelSante> pros = dataService.loadProfessionnels();
        
        // Toujours créer l'admin par défaut
        Administrateur admin = new Administrateur("admin", "admin");
        auth.register(admin);
        adminService.creerCompte(admin);

        if (!patients.isEmpty() || !pros.isEmpty()) {
            System.out.println("Chargement des données...");
            for (Patient p : patients) patientService.creerPatient(p);
            for (ProfessionnelSante p : pros) {
                adminService.creerCompte(p);
                auth.register(p);
            }
            
            // Charger consultations après avoir chargé patients et pros
            List<Consultation> consultations = dataService.loadConsultations(patientService.getPatients(), adminService.getProfessionnels());
           
            for(Consultation c : consultations) {
                consultationService.ajouterConsultationExistante(c);
            }

            System.out.println("✓ Données chargées: " + patients.size() + " patients, " + pros.size() + " professionnels.");
        } else {
            System.out.println("? Système initialisé. Aucune donnée sauvegardée.");
            sauvegarderDonnees();
        }
    }

    /**
     * Sauvegarde les données en mémoire dans les fichiers CSV
     */
    private static void sauvegarderDonnees() {
        dataService.savePatients(patientService.getPatients());
        dataService.saveProfessionnels(adminService.getProfessionnels());
        dataService.saveConsultations(consultationService.getConsultations());
        System.out.println("(Données sauvegardées)");
    }

    // --- Méthodes utilitaires pour la saisie sécurisée ---

    private static String lireChaine(String prompt) {
        System.out.print(prompt);
        return sc.nextLine().trim();
    }

    private static int lireEntier(String prompt) {
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

    private static LocalDateTime lireDate(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                String input = sc.nextLine().trim();
                return LocalDateTime.parse(input, DATE_FORMATTER);
            } catch (DateTimeParseException e) {
                System.out.println("❌ Format de date invalide. Utilisez 'yyyy-MM-dd HH:mm' (ex: 2023-12-25 14:30)");
            }
        }
    }
}
