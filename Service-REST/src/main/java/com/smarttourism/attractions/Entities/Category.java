package com.smarttourism.attractions.Entities;

public enum Category {
    MUSEUM("Musée"),
    MONUMENT("Monument"),
    PARK("Parc"),
    RESTAURANT("Restaurant"),
    BEACH("Plage"),
    SHOPPING("Shopping"),
    RELIGIOUS("Religieux"),
    HISTORICAL("Historique"),
    ENTERTAINMENT("Divertissement"),
    OTHER("Autre");
    
    private final String displayName;
    
    Category(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}