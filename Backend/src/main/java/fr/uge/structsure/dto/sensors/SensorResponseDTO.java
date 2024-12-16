package fr.uge.structsure.dto.sensors;

import java.time.LocalTime;

public class SensorResponseDTO {
    private Long id;
    private Long planId;
    private String controlChip;
    private String measureChip;
    private String name;
    private LocalTime installationDate;
    private String note;
    private Position position;
    private String state;
    private String lastState;

    public static class Position {
        private Double x;
        private Double y;

        public Position(Double x, Double y) {
            this.x = x;
            this.y = y;
        }

        // Getters and Setters

        public Double getX() {
            return x;
        }

        public void setX(Double x) {
            this.x = x;
        }

        public Double getY() {
            return y;
        }

        public void setY(Double y) {
            this.y = y;
        }
    }

    // Getters and Setters

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Long getPlanId() {
        return planId;
    }

    public void setPlanId(Long planId) {
        this.planId = planId;
    }

    public String getControlChip() {
        return controlChip;
    }

    public void setControlChip(String controlChip) {
        this.controlChip = controlChip;
    }

    public String getMeasureChip() {
        return measureChip;
    }

    public void setMeasureChip(String measureChip) {
        this.measureChip = measureChip;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalTime getInstallationDate() {
        return installationDate;
    }

    public void setInstallationDate(LocalTime installationDate) {
        this.installationDate = installationDate;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getLastState() {
        return lastState;
    }

    public void setLastState(String lastState) {
        this.lastState = lastState;
    }

    @Override
    public String toString() {
        return "SensorResponseDTO{" +
                "id=" + id +
                ", planId=" + planId +
                ", controlChip='" + controlChip + '\'' +
                ", measureChip='" + measureChip + '\'' +
                ", name='" + name + '\'' +
                ", installationDate=" + installationDate +
                ", note='" + note + '\'' +
                ", position=" + position +
                ", state='" + state + '\'' +
                ", lastState='" + lastState + '\'' +
                '}';
    }
}