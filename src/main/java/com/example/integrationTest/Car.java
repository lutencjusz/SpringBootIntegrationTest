package com.example.integrationTest;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
@Entity
@Table(name = "cars")
public class Car extends RepresentationModel<Car> {

    @Id
    @GeneratedValue
    @JsonProperty("id")
    private Long id;
    private String make;
    private String model;
    private String color;
    private Long year;

}
