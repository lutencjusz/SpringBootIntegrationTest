package com.example.integrationTest.services;

import com.example.integrationTest.Car;
import com.example.integrationTest.CarRepository;
import com.example.integrationTest.exceptions.CarNotFoundException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class CarService {

    private static final String CARS_JSON = """
                [
                    {"id":1,"make":"Audi","model":"A4","color":"black","year":2019},
                    {"id":2,"make":"BMW","model":"X5","color":"white","year":2020},
                    {"id":3,"make":"Mercedes","model":"E220","color":"black","year":2018}
                ]
            """;

    @Autowired
    private CarRepository carRepository;

    @PostConstruct
    public void loadData() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Car[] cars = objectMapper.readValue(CARS_JSON, Car[].class);
            carRepository.saveAll(Arrays.asList(cars));
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to load car data", e);
        }
    }

    public CarService(CarRepository carRepository) {
        this.carRepository = carRepository;
    }

    public Car saveCar(Car car) {
        return carRepository.save(car);
    }

    public Car getCarById(Long id) {
        return carRepository.findById(id).orElseThrow(() -> new CarNotFoundException(id));
    }

    public List<Car> getAllCars() {
        return carRepository.findAll();
    }

}
