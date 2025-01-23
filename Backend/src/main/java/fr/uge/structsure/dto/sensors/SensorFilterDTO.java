package fr.uge.structsure.dto.sensors;

import java.time.LocalTime;

public class SensorFilterDTO {
    private Long structureId;
    private String tri;
    private String ordre;
    private String filtreEtat;
    private LocalTime dateInstallationMin;
    private LocalTime dateInstallationMax;

    // Getters and Setters
    public Long getStructureId() {
        return structureId;
    }

    public void setStructureId(Long structureId) {
        this.structureId = structureId;
    }

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

    public LocalTime getDateInstallationMin() {
        return dateInstallationMin;
    }

    public void setDateInstallationMin(LocalTime dateInstallationMin) {
        this.dateInstallationMin = dateInstallationMin;
    }

    public LocalTime getDateInstallationMax() {
        return dateInstallationMax;
    }

    public void setDateInstallationMax(LocalTime dateInstallationMax) {
        this.dateInstallationMax = dateInstallationMax;
    }
}