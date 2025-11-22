package com.medipass.models;

public class Patient extends Personne {
    private String numeroSecuriteSociale;
    private String groupeSanguin;
    private DossierMedical dossierMedical;
    
    private static int compteur = 1000;
    
    public Patient() {
        super();
        this.id = compteur++;
        this.dossierMedical = null;
    }
    
    // Getters
    public String getNumeroSSI() { 
        return numeroSecuriteSociale; 
    }
    public String getGroupeSanguin() { 
        return groupeSanguin; 
    }
    public DossierMedical getDossierMedical() { 
        return dossierMedical; 
    }
    
    // Setters
    public void setNumeroSecuriteSociale(String nss) { 
        this.numeroSecuriteSociale = nss; 
    }
    
    public void setGroupeSanguin(String gs) { 
        this.groupeSanguin = gs; 
    }
    
    public void setDossierMedical(DossierMedical d) { 
        this.dossierMedical = d; 
    }
    
    public void afficherInfos() {
        System.out.println("\n=== Informations Patient ===");
        System.out.println("ID: " + id);
        System.out.println("Nom: " + nom + " " + prenom);
        System.out.println("Date de naissance: " + dateNaissance);
        System.out.println("Sexe: " + sexe);
        System.out.println("N° Sécurité Sociale: " + numeroSecuriteSociale);
        System.out.println("Groupe sanguin: " + groupeSanguin);
        System.out.println("Téléphone: " + telephone);
        if (email != null && !email.isEmpty()) {
            System.out.println("Email: " + email);
        }
        System.out.println("Adresse: " + adresse);
    }
}