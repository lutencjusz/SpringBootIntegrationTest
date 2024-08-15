package com.example.integrationTest;

import com.example.integrationTest.services.CarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

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
    public ResponseEntity<CollectionModel<Car>> getAllCars() {
        List<Car> allCars = carService.getAllCars();
        allCars.forEach(car -> car.add(linkTo(CarController.class).slash(car.getId()).withSelfRel()));
        Link link = linkTo(CarController.class).withSelfRel();
        CollectionModel<Car> carCollectionModel = CollectionModel.of(allCars, link);
        return ResponseEntity.ok(carCollectionModel);
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
