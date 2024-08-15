package com.example.integrationTest.controller;

import com.example.integrationTest.Car;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Objects;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * UWAGA: Musi być uruchomiony Docker Desktop i nie musi być uruchamiana baza MSSQL
 */

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
@Transactional
class CarControllerIntegrationTests {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:16:0");

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    private int getActualFreeId() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/cars"))
                .andExpect(status().is2xxSuccessful())
                .andReturn();
        String jsonResponse = mvcResult.getResponse().getContentAsString();
        JsonNode rootNode = objectMapper.readTree(jsonResponse);
        JsonNode carsNode = rootNode.path("_embedded").path("cars");
        Car[] cars = objectMapper.readValue(carsNode.toString(), Car[].class);
        return carsNode.size() + 1;
    }

    @Test
    void shouldReturnSelectCar() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/cars/1"))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.make").value("Audi"))
                .andExpect(jsonPath("$.model").value("A4"))
                .andExpect(jsonPath("$.color").value("black"))
                .andExpect(jsonPath("$.year").value(2019))
                .andReturn();

        Car car = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Car.class);
        System.out.println("Pobrany samochód: " + car.toString());
        Assertions.assertEquals("Audi", car.getMake());
        Assertions.assertEquals("A4", car.getModel());
        Assertions.assertEquals("black", car.getColor());
        Assertions.assertEquals(2019, car.getYear());
    }

    @Test
    void shouldReturnAllCars() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/cars"))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andReturn();
        String jsonResponse = mvcResult.getResponse().getContentAsString();
        JsonNode rootNode = objectMapper.readTree(jsonResponse);
        JsonNode carsNode = rootNode.path("_embedded").path("cars");
        Car[] cars = objectMapper.readValue(carsNode.toString(), Car[].class);
        Assertions.assertTrue(cars.length >= 3);
    }

    @Test
    void shouldReturn4xxWhenGet() throws Exception {
        int actualFreeId = getActualFreeId();
        MvcResult mvcResult = mockMvc.perform(get("/cars/" + actualFreeId))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andReturn();
        String actualMessage = Objects.requireNonNull(mvcResult.getResolvedException()).getMessage();
        Assertions.assertEquals("Nie znaleziono samochodu o id: " + actualFreeId, actualMessage);

    }

    @Test
    void shouldReturnCorrectValueInFirstCar() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/cars/1"))
                .andExpect(status().is2xxSuccessful())
                .andDo(print())
                .andReturn();
        Car actualCar = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Car.class);
        Assertions.assertEquals("Audi", actualCar.getMake());
        Assertions.assertEquals("A4", actualCar.getModel());
        Assertions.assertEquals("black", actualCar.getColor());
        Assertions.assertEquals(2019, actualCar.getYear());
    }

    @Test
    void shouldAddCarAndCheckActualFreeId() throws Exception {
        int actualFreeId = getActualFreeId();
        Car car = new Car((long) actualFreeId, "Opel", "Astra", "red", 2015L);
        mockMvc.perform(post("/cars")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(car)))
                .andDo(print())
                .andExpect(status().isOk());

        mockMvc.perform(get("/cars/" + actualFreeId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.make").value("Opel"))
                .andExpect(jsonPath("$.model").value("Astra"))
                .andExpect(jsonPath("$.color").value("red"))
                .andExpect(jsonPath("$.year").value(2015));
    }
}
