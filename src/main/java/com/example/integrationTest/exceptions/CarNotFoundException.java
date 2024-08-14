package com.example.integrationTest.exceptions;

public class CarNotFoundException extends RuntimeException {
    public CarNotFoundException(Long id) {
        super("Nie znaleziono samochodu o id: " + id);
    }
}
