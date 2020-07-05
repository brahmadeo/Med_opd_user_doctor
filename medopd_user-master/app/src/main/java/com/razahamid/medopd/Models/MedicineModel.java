package com.razahamid.medopd.Models;

public class MedicineModel {
    public String medicineName;
    public String dosageOne;
    public String dosageTwo;
    public String dosageThree;
    public String frequency;
    public String durationCount;
    public String durationLabel;
    public String instruction;

    public String getDosageMsg() {
        return "Morning " + this.dosageOne + " , After-Noon " + this.dosageTwo + " , Night " + this.dosageThree;
    }

    public String getDurationMsg() {
        return this.frequency + " for " + this.durationCount + " " + this.durationLabel;
    }


}
