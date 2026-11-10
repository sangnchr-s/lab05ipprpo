package com.autoservice.model;

public class Car {

    private final String id;
    private final String brand;
    private final String plateNumber;

    public Car(String id, String brand, String plateNumber) {
        this.id = id;
        this.brand = brand;
        this.plateNumber = plateNumber;
    }

    public String getId() {
        return id;
    }

    public String getBrand() {
        return brand;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    @Override
    public String toString() {
        return brand + " (" + plateNumber + ")";
    }
}
