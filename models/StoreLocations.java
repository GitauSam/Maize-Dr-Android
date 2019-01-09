package org.tensorflow.demo.models;


public class StoreLocations {
    private String storeName, locationDetails, storeImageUrl;

    public StoreLocations(String storeName, String locationDetails, String storeImageUrl) {
        this.storeName = storeName;
        this.locationDetails = locationDetails;
        this.storeImageUrl = storeImageUrl;
    }

    public String getStoreName() {
        return storeName;
    }

    public String getLocationDetails() {
        return locationDetails;
    }

    public String getStoreImageUrl() {
        return storeImageUrl;
    }
}
