package com.example.integrationTest.controllers;

import com.example.integrationTest.models.Car;
import com.example.integrationTest.services.CarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public Car saveCar(@RequestBody Car car) {
        return carService.saveCar(car);
    }
}
