package com.coalemission.beans;
import java.sql.*;

// Base class
class MiningActivity {
    protected String mineID;
    protected String equipment;
    protected double fuelConsumed;
    protected double emissionFactor;

    public MiningActivity(String mineID, String equipment, double fuelConsumed, double emissionFactor) {
        this.mineID = mineID;
        this.equipment = equipment;
        this.fuelConsumed = fuelConsumed;
        this.emissionFactor = emissionFactor;
    }

    // here we calculate emissions
    public double calculateEmission() {
        return fuelConsumed * emissionFactor;
    }

    // dispalying the information
    public void displayInfo() {
        System.out.printf("%-10s %-15s %-10.2f %-10.2f %-10.2f\n",
                mineID, equipment, fuelConsumed, emissionFactor, calculateEmission());
    }
}

// Subclasses for specific mining activities (optional override)
class Excavation extends MiningActivity {
    public Excavation(String mineID, String equipment, double fuelConsumed, double emissionFactor) {
        super(mineID, equipment, fuelConsumed, emissionFactor);
    }
}

class Transportation extends MiningActivity {
    public Transportation(String mineID, String equipment, double fuelConsumed, double emissionFactor) {
        super(mineID, equipment, fuelConsumed, emissionFactor);
    }
}

class Drilling extends MiningActivity {
    public Drilling(String mineID, String equipment, double fuelConsumed, double emissionFactor) {
        super(mineID, equipment, fuelConsumed, emissionFactor);
    }
}

class Hauling extends MiningActivity {
    public Hauling(String mineID, String equipment, double fuelConsumed, double emissionFactor) {
        super(mineID, equipment, fuelConsumed, emissionFactor);
    }
}
