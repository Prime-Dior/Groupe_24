package com.medipass.user;

/**
 * Utilisateur de base (compte).
 * Pour simplifier, mot de passe stocké en clair (NE PAS faire en production).
 */
public class Utilisateur {
    protected String loginID;
    protected String motDePasse;
    protected String role;
    protected boolean actif;
    protected String nom;
    protected String prenom;
    protected String email;
    protected String telephone;

    public Utilisateur(String loginID, String motDePasse, String role) {
        this(loginID, motDePasse, role, "", "");
    }

    public Utilisateur(String loginID, String motDePasse, String role, String nom, String prenom) {
        this.loginID = loginID;
        this.motDePasse = motDePasse;
        this.role = role;
        this.nom = nom;
        this.prenom = prenom;
        this.actif = true;
        this.email = "";
        this.telephone = "";
    }

    public String getLoginID(){ return loginID; }
    public String getPassword(){ return motDePasse; }
    public String getRole(){ return role; }
    public boolean isActif(){ return actif; }
    public String getNom(){ return nom; }
    public String getPrenom(){ return prenom; }
    public String getEmail(){ return email; }
    public String getTelephone(){ return telephone; }

    public void setEmail(String email){ this.email = email; }
    public void setTelephone(String telephone){ this.telephone = telephone; }
    public void setRole(String role){ this.role = role; }

    // Vérifie identifiants (simple)
    public boolean seConnecter(String loginID, String mdp){
        return this.loginID.equals(loginID) && this.motDePasse.equals(mdp) && actif;
    }

    public void desactiver(){ this.actif = false; }
    public void activer(){ this.actif = true; }

    @Override
    public String toString(){
        return String.format("[%s] %s %s (%s) - %s", loginID, nom, prenom, role, actif ? "Actif" : "Inactif");
    }
}

