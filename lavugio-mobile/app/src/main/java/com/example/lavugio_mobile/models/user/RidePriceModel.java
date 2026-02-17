package com.example.lavugio_mobile.models.user;

import com.google.gson.annotations.SerializedName;

public class RidePriceModel {
    
    @SerializedName("standard")
    private Double standard;
    
    @SerializedName("luxury")
    private Double luxury;
    
    @SerializedName("combi")
    private Double combi;
    
    @SerializedName("kilometer")
    private Double kilometer;

    public RidePriceModel() {}

    public RidePriceModel(Double standard, Double luxury, Double combi, Double kilometer) {
        this.standard = standard;
        this.luxury = luxury;
        this.combi = combi;
        this.kilometer = kilometer;
    }

    public Double getStandard() { 
        return standard; 
    }
    
    public void setStandard(Double standard) { 
        this.standard = standard; 
    }

    public Double getLuxury() { 
        return luxury; 
    }
    
    public void setLuxury(Double luxury) { 
        this.luxury = luxury; 
    }

    public Double getCombi() { 
        return combi; 
    }
    
    public void setCombi(Double combi) { 
        this.combi = combi; 
    }

    public Double getKilometer() { 
        return kilometer; 
    }
    
    public void setKilometer(Double kilometer) { 
        this.kilometer = kilometer; 
    }

    // Helper methods
    public double getPriceForType(String type) {
        if (type == null) return 0.0;
        
        switch (type.toLowerCase()) {
            case "standard": 
                return standard != null ? standard : 0.0;
            case "luxury": 
                return luxury != null ? luxury : 0.0;
            case "combi": 
                return combi != null ? combi : 0.0;
            default: 
                return 0.0;
        }
    }

    public void setPriceForType(String type, double price) {
        if (type == null) return;
        
        switch (type.toLowerCase()) {
            case "standard": 
                standard = price; 
                break;
            case "luxury": 
                luxury = price; 
                break;
            case "combi": 
                combi = price; 
                break;
        }
    }

    /**
     * Validates that all prices are positive and not null
     * @return true if valid, false otherwise
     */
    public boolean isValid() {
        return standard != null && standard > 0
            && luxury != null && luxury > 0
            && combi != null && combi > 0
            && kilometer != null && kilometer > 0;
    }

    /**
     * Get validation error message
     * @return error message or null if valid
     */
    public String getValidationError() {
        if (standard == null || standard <= 0) {
            return "Standard price must be greater than 0";
        }
        if (luxury == null || luxury <= 0) {
            return "Luxury price must be greater than 0";
        }
        if (combi == null || combi <= 0) {
            return "Combi price must be greater than 0";
        }
        if (kilometer == null || kilometer <= 0) {
            return "Kilometer price must be greater than 0";
        }
        return null;
    }
}