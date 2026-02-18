package com.example.lavugio_mobile.models;

import java.time.LocalDateTime;
import java.util.List;

public class ScheduledRideModel {
    private long rideId;
    private String status;
    private String startAddress;
    private String endAddress;
    private LocalDateTime scheduledTime;
    private String passengerName;
    private Double price;
    private Double distance;
    private List<Coordinates> checkpoints;
    private Boolean panicked;

    // Getters and Setters
    public long getRideId() { 
        return rideId; 
    }
    
    public void setRideId(long rideId) { 
        this.rideId = rideId; 
    }

    public String getStatus() { 
        return status; 
    }
    
    public void setStatus(String status) { 
        this.status = status; 
    }

    public String getStartAddress() { 
        return startAddress; 
    }
    
    public void setStartAddress(String startAddress) { 
        this.startAddress = startAddress; 
    }

    public String getEndAddress() { 
        return endAddress; 
    }
    
    public void setEndAddress(String endAddress) { 
        this.endAddress = endAddress; 
    }

    public LocalDateTime getScheduledTime() { 
        return scheduledTime; 
    }
    
    public void setScheduledTime(LocalDateTime scheduledTime) { 
        this.scheduledTime = scheduledTime; 
    }

    public String getPassengerName() { 
        return passengerName; 
    }
    
    public void setPassengerName(String passengerName) { 
        this.passengerName = passengerName; 
    }

    public Double getPrice() { 
        return price; 
    }
    
    public void setPrice(Double price) { 
        this.price = price; 
    }

    public Double getDistance() { 
        return distance; 
    }
    
    public void setDistance(Double distance) { 
        this.distance = distance; 
    }

    public List<Coordinates> getCheckpoints() { 
        return checkpoints; 
    }
    
    public void setCheckpoints(List<Coordinates> checkpoints) { 
        this.checkpoints = checkpoints; 
    }

    public Boolean getPanicked() { 
        return panicked; 
    }
    
    public void setPanicked(Boolean panicked) { 
        this.panicked = panicked; 
    }
}