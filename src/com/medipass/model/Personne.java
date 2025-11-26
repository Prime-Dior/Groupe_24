package com.medipass.model;

import java.time.LocalDate;
import java.time.Period;

/**
 * Classe abstraite représentant une personne.
 * Contient les champs de base et des helpers simples.
 */
public abstract class Personne {
    protected int id;
    protected String nom;
    protected String prenom;
    protected LocalDate dateNaissance;
    protected char sexe;
    protected String adresse;
    protected String telephone;
    protected String email;

    public Personne(int id, String nom, String prenom) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
    }

    // Getters simples
    public int getId() { return id; }
    public String getNom() { return nom; }
    public String getPrenom() { return prenom; }

    // Retourne l'âge basé sur dateNaissance ou -1 si inconnu
    public int getAge() {
        if (dateNaissance == null) return -1;
        return Period.between(dateNaissance, LocalDate.now()).getYears();
    }

    // Setters utilitaires
    public void setNom(String n){ this.nom = n; }
    public void setPrenom(String p){ this.prenom = p; }
    public void setDateNaissance(LocalDate d){ this.dateNaissance = d; }
    public void setSexe(char s){ this.sexe = s; }
    public void setAdresse(String a){ this.adresse = a; }
    public void setTelephone(String t){ this.telephone = t; }
    public void setEmail(String e){ this.email = e; }
}
