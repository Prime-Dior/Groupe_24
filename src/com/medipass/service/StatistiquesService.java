package com.medipass.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.medipass.model.Patient;
import com.medipass.user.ProfessionnelSante;
import com.medipass.model.Consultation;

/*
 * Service de statistiques basiques.
 */
public class StatistiquesService {

    public int getNombrePatients(List<Patient> patients){
        return patients == null ? 0 : patients.size();
    }

    public int getNombreProfessionnels(List<ProfessionnelSante> pros){
        return pros == null ? 0 : pros.size();
    }

    public Map<String, Long> getProfessionnelsParSpecialite(List<ProfessionnelSante> pros){
        if(pros == null) return java.util.Collections.emptyMap();
        return pros.stream().collect(Collectors.groupingBy(ProfessionnelSante::getSpecialite, Collectors.counting()));
    }

    public int getConsultationsParPeriode(List<Consultation> consultations, java.time.LocalDateTime debut, java.time.LocalDateTime fin){
        if(consultations == null) return 0;
        return (int) consultations.stream().filter(c -> !c.getDateHeure().isBefore(debut) && !c.getDateHeure().isAfter(fin)).count();
    }

    public String afficherStatistiques(int nbPatients, int nbPros, int nbConsultations, List<Consultation> consultations, List<ProfessionnelSante> pros) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n=== STATISTIQUES DU SYSTÈME ===\n");
        sb.append(String.format("Nombre total de patients : %d\n", nbPatients));
        sb.append(String.format("Nombre total de professionnels : %d\n", nbPros));
        sb.append(String.format("Nombre total de consultations : %d\n", nbConsultations));
        
        sb.append("\n--- Répartition par spécialité ---\n");
        Map<String, Long> parSpecialite = getProfessionnelsParSpecialite(pros);
        if (parSpecialite.isEmpty()) {
            sb.append("Aucune donnée disponible.\n");
        } else {
            parSpecialite.forEach((spec, count) -> 
                sb.append(String.format("- %s : %d\n", spec, count)));
        }
        
        return sb.toString();
    }
}
