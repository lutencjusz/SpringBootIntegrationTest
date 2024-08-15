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
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

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
        List<Car> cars = carService.getAllCars();
        List<EntityModel<Car>> halCars = cars.stream()
                .map(car -> EntityModel.of(car, linkTo(CarController.class).slash(car.getId()).withSelfRel()))
                .collect(Collectors.toList());
        Link link = linkTo(CarController.class).withSelfRel();
        return CollectionModel.of(halCars, link);
    }

    @GetMapping("/{id}")
    public ResponseEntity getCarById(@PathVariable("id") Long id) {
        Link link = linkTo(CarController.class).slash(id).withSelfRel();
        Optional<Car> carById = Optional.ofNullable(carService.getCarById(id));
        return carById.map(car -> ResponseEntity.ok().body(car.add(link)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    @PostMapping
    public Car saveCar(@RequestBody Car car) {
        return carService.saveCar(car);
    }
}
