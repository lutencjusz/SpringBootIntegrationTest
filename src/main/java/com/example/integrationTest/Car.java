package com.example.integrationTest;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.hateoas.RepresentationModel;

@NoArgsConstructor
@ToString
@Getter
@Setter
@Entity
@Table(name = "cars")
public class Car extends RepresentationModel<Car> {
    @Id
    @GeneratedValue
    private Long id;
    private String make;
    private String model;
    private String color;
    private Long year;

    public Car(Long id, String make, String model, String color, Long year) {
        if (make == null) {
            throw new IllegalArgumentException("Marka nie może być pusta");
        }
        if (model == null) {
            throw new IllegalArgumentException("Model nie może być pusty");
        }
        if (color == null) {
            throw new IllegalArgumentException("Kolor nie może być pusty");
        }
        if (year == null) {
            throw new IllegalArgumentException("Rok produkcji nie może być pusty");
        }
    }
}
