package com.medipass.security;

import java.util.HashMap;
import java.util.Map;
import com.medipass.user.Utilisateur;

/**
 * Service d'authentification très simple (stockage en mémoire).
 * Fournit login/logout et recherche de comptes.
 */
public class AuthentificationService {
    private final Map<String, Utilisateur> users = new HashMap<>();
    private Utilisateur currentUser = null;

    public void register(Utilisateur u){
        users.put(u.getLoginID(), u);
    }

    public boolean login(String login, String mdp){
        Utilisateur u = users.get(login);
        if(u != null && u.seConnecter(login, mdp)){
            currentUser = u;
            return true;
        }
        return false;
    }

    public void logout(){ currentUser = null; }
    public Utilisateur getCurrentUser(){ return currentUser; }
    public Utilisateur findUser(String login){ return users.get(login); }
}
