package com.medipass.service;

import java.util.ArrayList;
import java.util.List;

import com.medipass.user.Administrateur;
import com.medipass.user.ProfessionnelSante;
import com.medipass.user.Utilisateur;

/**
 * Service de gestion administrative.
 * Permet de créer, modifier et supprimer des comptes et des droits d'accès.
 */
public class AdministrateurService {
    private final List<Utilisateur> utilisateurs = new ArrayList<>();
    private final List<ProfessionnelSante> professionnels = new ArrayList<>();

    /**
     * Crée un nouveau compte utilisateur
     */
    public boolean creerCompte(Utilisateur utilisateur) {
        if (utilisateur == null || findUtilisateur(utilisateur.getLoginID()) != null) {
            return false;
        }
        utilisateurs.add(utilisateur);
        
        // Si c'est un professionnel, l'ajouter aussi à la liste spécifique
        if (utilisateur instanceof ProfessionnelSante) {
            professionnels.add((ProfessionnelSante) utilisateur);
        }
        
        return true;
    }

    /**
     * Supprime un compte utilisateur
     */
    public boolean supprimerCompte(String loginID) {
        Utilisateur utilisateur = findUtilisateur(loginID);
        if (utilisateur == null) {
            return false;
        }
        
        utilisateurs.remove(utilisateur);
        if (utilisateur instanceof ProfessionnelSante) {
            professionnels.remove(utilisateur);
        }
        
        return true;
    }

    /**
     * Recherche un utilisateur par login
     */
    public Utilisateur findUtilisateur(String loginID) {
        return utilisateurs.stream()
                .filter(u -> u.getLoginID().equalsIgnoreCase(loginID))
                .findFirst().orElse(null);
    }

    /**
     * Recherche un professionnel par login
     */
    public ProfessionnelSante findProfessionnel(String loginID) {
        return professionnels.stream()
                .filter(p -> p.getLoginID().equalsIgnoreCase(loginID))
                .findFirst().orElse(null);
    }

    /**
     * Recherche un professionnel par spécialité
     */
    public List<ProfessionnelSante> findProfessionnelsBySpecialite(String specialite) {
        List<ProfessionnelSante> result = new ArrayList<>();
        for (ProfessionnelSante p : professionnels) {
            if (p.getSpecialite().equalsIgnoreCase(specialite)) {
                result.add(p);
            }
        }
        return result;
    }

    /**
     * Récupère tous les utilisateurs
     */
    public List<Utilisateur> getUtilisateurs() {
        return new ArrayList<>(utilisateurs);
    }

    /**
     * Récupère tous les professionnels
     */
    public List<ProfessionnelSante> getProfessionnels() {
        return new ArrayList<>(professionnels);
    }

    /**
     * Modifie les droits/rôle d'un utilisateur
     */
    public boolean modifierRole(String loginID, String nouveauRole) {
        Utilisateur utilisateur = findUtilisateur(loginID);
        if (utilisateur == null) {
            return false;
        }
        utilisateur.setRole(nouveauRole);
        return true;
    }

    /**
     * Active un compte utilisateur
     */
    public boolean activerCompte(String loginID) {
        Utilisateur utilisateur = findUtilisateur(loginID);
        if (utilisateur == null) {
            return false;
        }
        utilisateur.activer();
        return true;
    }

    /**
     * Désactive un compte utilisateur
     */
    public boolean desactiverCompte(String loginID) {
        Utilisateur utilisateur = findUtilisateur(loginID);
        if (utilisateur == null) {
            return false;
        }
        utilisateur.desactiver();
        return true;
    }

    /**
     * Modifie les informations de contact d'un utilisateur
     */
    public boolean modifierContactUtilisateur(String loginID, String email, String telephone) {
        Utilisateur utilisateur = findUtilisateur(loginID);
        if (utilisateur == null) {
            return false;
        }
        if (email != null) {
            utilisateur.setEmail(email);
        }
        if (telephone != null) {
            utilisateur.setTelephone(telephone);
        }
        return true;
    }

    /**
     * Obtient le nombre total de professionnels
     */
    public int getNombreProfessionnels() {
        return professionnels.size();
    }

    /**
     * Affiche les informations d'un utilisateur
     */
    public String afficherUtilisateur(String loginID) {
        Utilisateur utilisateur = findUtilisateur(loginID);
        if (utilisateur == null) {
            return "Utilisateur non trouvé";
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("=== INFORMATIONS UTILISATEUR ===\n");
        sb.append("Login: ").append(utilisateur.getLoginID()).append("\n");
        sb.append("Nom: ").append(utilisateur.getNom()).append("\n");
        sb.append("Prénom: ").append(utilisateur.getPrenom()).append("\n");
        sb.append("Email: ").append(utilisateur.getEmail()).append("\n");
        sb.append("Téléphone: ").append(utilisateur.getTelephone()).append("\n");
        sb.append("Rôle: ").append(utilisateur.getRole()).append("\n");
        sb.append("Statut: ").append(utilisateur.isActif() ? "Actif" : "Inactif").append("\n");
        
        if (utilisateur instanceof ProfessionnelSante) {
            ProfessionnelSante pro = (ProfessionnelSante) utilisateur;
            sb.append("Spécialité: ").append(pro.getSpecialite()).append("\n");
            sb.append("Numéro d'ordre: ").append(pro.getNumeroOrdre()).append("\n");
            sb.append("Consultations programmées: ").append(pro.getNombreConsultations()).append("\n");
        }
        
        return sb.toString();
    }

    /**
     * Affiche tous les professionnels
     */
    public String afficherProfessionnels() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== LISTE DES PROFESSIONNELS ===\n");
        if (professionnels.isEmpty()) {
            sb.append("Aucun professionnel enregistré\n");
        } else {
            for (ProfessionnelSante p : professionnels) {
                sb.append(p).append("\n");
            }
        }
        return sb.toString();
    }
}
