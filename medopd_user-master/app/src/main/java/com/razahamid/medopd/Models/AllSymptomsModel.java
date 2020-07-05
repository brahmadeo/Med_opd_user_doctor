package com.razahamid.medopd.Models;

import com.google.android.material.checkbox.MaterialCheckBox;

public class AllSymptomsModel {
    String Symptoms;
    MaterialCheckBox materialCheckBox;

    public AllSymptomsModel(String symptoms) {
        Symptoms = symptoms;
    }

    public String getSymptoms() {
        return Symptoms;
    }

    public void setSymptoms(String symptoms) {
        Symptoms = symptoms;
    }

    public MaterialCheckBox getMaterialCheckBox() {
        return materialCheckBox;
    }

    public void setMaterialCheckBox(MaterialCheckBox materialCheckBox) {
        this.materialCheckBox = materialCheckBox;
    }
}
