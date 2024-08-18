package com.example.integrationTest.services;

import com.example.integrationTest.exceptions.CarNotFoundException;
import com.example.integrationTest.models.Car;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.testcontainers.shaded.org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;

@SpringBootTest
@Transactional
class CarServiceTest {

    @Autowired
    private CarService carService;

    @Test
    void ShouldSaveCarAndSameCarAgain() {
        String make = randomAlphabetic(5);
        Car car = new Car(null, make, "A4", "black", 2019L);
        Car savedCar = carService.saveCar(car);
        assertNotNull(savedCar);
        assertEquals(car.getMake(), savedCar.getMake());
        assertEquals(car.getModel(), savedCar.getModel());
        assertEquals(car.getColor(), savedCar.getColor());
        assertEquals(car.getYear(), savedCar.getYear());
        Car sameSavedCar = carService.saveCar(car);
        assertNotNull(sameSavedCar);
        assertEquals(car.getMake(), sameSavedCar.getMake());
        assertEquals(car.getModel(), sameSavedCar.getModel());
        assertEquals(car.getColor(), sameSavedCar.getColor());
        assertEquals(car.getYear(), sameSavedCar.getYear());
    }

    @Test
    void shouldGetNotExistsCarAndGenerateCarNotFoundException() {
        List<Car> cars = carService.getAllCars();
        Long nextFreeId = (long) (cars.size() + 1);
        try {
            Car car = carService.getCarById(nextFreeId);
            fail("Serwis zwrócił samochód, który nie istnieje");
        } catch (CarNotFoundException e) {
            assertEquals(e.getMessage(), "Nie znaleziono samochodu o id: " + nextFreeId);
            System.out.println(e.getMessage());
        }
    }

    @Test
    void shouldGetCarById() {
        List<Car> cars = carService.getAllCars();
        for (Car car : cars) {
            Car carById = carService.getCarById(car.getId());
            assertNotNull(carById);
            assertEquals(car.getMake(), carById.getMake());
            assertEquals(car.getModel(), carById.getModel());
            assertEquals(car.getColor(), carById.getColor());
            assertEquals(car.getYear(), carById.getYear());
        }
        Long nextFreeId = (long) (cars.size() + 1);
        String make = randomAlphabetic(5);
        Car car = new Car(nextFreeId, make, "A4", "black", 2019L);
        Car savedCar = carService.saveCar(car);
        Car carById = carService.getCarById(nextFreeId);
        assertNotNull(carById);
        assertEquals(car.getMake(), carById.getMake());
        assertEquals(car.getModel(), carById.getModel());
        assertEquals(car.getColor(), carById.getColor());
        assertEquals(car.getYear(), carById.getYear());
    }

    @Test
    void shouldGetAllCars() {
        List<Car> cars = carService.getAllCars();
        assertNotNull(cars);
        assertTrue(cars.size() >= 3);
    }
}