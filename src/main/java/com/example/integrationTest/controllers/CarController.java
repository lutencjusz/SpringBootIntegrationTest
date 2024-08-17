package com.example.integrationTest.controllers;

import com.example.integrationTest.models.Car;
import com.example.integrationTest.services.CarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/cars")
public class CarController {

    private final CarService carService;

    @Autowired
    public CarController(CarService carService) {
        this.carService = carService;
    }

    @GetMapping
    public CollectionModel<EntityModel<Car>> getAllCars() {
        List<EntityModel<Car>> cars = carService.getAllCars().stream()
                .map(car -> EntityModel.of(car, linkTo(methodOn(CarController.class).getCarById(car.getId())).withSelfRel()))
                .toList();
        return CollectionModel.of(cars, linkTo(methodOn(CarController.class).getAllCars()).withSelfRel());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Car>> getCarById(@PathVariable("id") Long id) {
        Link link = linkTo(CarController.class).slash(id).withSelfRel();
        Optional<Car> carById = Optional.ofNullable(carService.getCarById(id));
        return carById.map(car -> ResponseEntity.ok().body(EntityModel.of(car, link)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Car saveCar(@Valid @RequestBody Car car) {
        if (car.getId() != null) {
            throw new IllegalArgumentException("Id nie powinno być podane");
        }
        if (car.getMake() == null || car.getModel() == null || car.getColor() == null || car.getYear() == null) {
            throw new IllegalArgumentException("Wszystkie pola muszą być wypełnione");
        }
        List<Car> cars = carService.getAllCars();
        for (Car c : cars) {
            if (c.getMake().equals(car.getMake()) &&
                    c.getModel().equals(car.getModel()) &&
                    c.getColor().equals(car.getColor()) &&
                    c.getYear().equals(car.getYear())) {
                throw new IllegalArgumentException("Samochód już istnieje");
            }
        }
        return carService.saveCar(car);
    }
}
