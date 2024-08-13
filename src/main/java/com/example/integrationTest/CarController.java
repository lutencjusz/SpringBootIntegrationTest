package com.example.integrationTest;

import com.example.integrationTest.services.CarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cars")
public class CarController {

    private final CarService carService;

    @Autowired
    public CarController(CarService carService) {
        this.carService = carService;
    }

    @GetMapping
    public Iterable<Car> getAllCars() {
        return carService.getAllCars();
    }

    @GetMapping("/{id}")
    public Car getCarById(@PathVariable("id") Long id) {
        return carService.getCarById(id);
    }
    @PostMapping
    public Car saveCar(Car car) {
        return carService.saveCar(car);
    }
}
