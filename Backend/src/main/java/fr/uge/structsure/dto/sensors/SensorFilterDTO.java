package fr.uge.structsure.dto.sensors;

import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;

public class SensorFilterDTO {
    @Pattern(regexp = "nom|etat|dateDerniereInterrogation|dateInstallation", message = "Critère de tri invalide")
    private String tri;

    @Pattern(regexp = "asc|desc", message = "Ordre invalide")
    private String ordre;

    @Pattern(regexp = "actif|archivé|défaillant", message = "Filtre d'état invalide")
    private String filtreEtat;
    private LocalDate dateInstallationMin;
    private LocalDate dateInstallationMax;

    // Getters and Setters
    public String getTri() {
        return tri;
    }

    public void setTri(String tri) {
        this.tri = tri;
    }

    public String getOrdre() {
        return ordre;
    }

    public void setOrdre(String ordre) {
        this.ordre = ordre;
    }

    public String getFiltreEtat() {
        return filtreEtat;
    }

    public void setFiltreEtat(String filtreEtat) {
        this.filtreEtat = filtreEtat;
    }

    public LocalDate getDateInstallationMin() {
        return dateInstallationMin;
    }

    public void setDateInstallationMin(LocalDate dateInstallationMin) {
        this.dateInstallationMin = dateInstallationMin;
    }

    public LocalDate getDateInstallationMax() {
        return dateInstallationMax;
    }

    public void setDateInstallationMax(LocalDate dateInstallationMax) {
        this.dateInstallationMax = dateInstallationMax;
    }

    @Override
    public String toString() {
        return "SensorFilterDTO{" +
                "tri='" + tri + '\'' +
                ", ordre='" + ordre + '\'' +
                ", filtreEtat='" + filtreEtat + '\'' +
                ", dateInstallationMin=" + dateInstallationMin +
                ", dateInstallationMax=" + dateInstallationMax +
                '}';
    }
}