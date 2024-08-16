package com.example.integrationTest.controller;

import com.example.integrationTest.models.Car;
import com.example.integrationTest.models.MyUser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * UWAGA: Musi być uruchomiony Docker Desktop i nie musi być uruchamiana baza MSSQL
 */

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Testcontainers
@AutoConfigureMockMvc
//@Transactional
class CarControllerIntegrationTests {

    private static String jwtUser;
    private final MyUser user = new MyUser(1L, "user", "user", "USER");

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:16:0");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private int getActualFreeId() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/cars")
                        .header("Authorization", "Bearer " + jwtUser))
                .andExpect(status().is2xxSuccessful())
                .andReturn();
        String jsonResponse = mvcResult.getResponse().getContentAsString();
        JsonNode rootNode = objectMapper.readTree(jsonResponse);
        JsonNode carsNode = rootNode.path("_embedded").path("cars");
        return carsNode.size() + 1;
    }

    private String getJwt(MyUser myUser) throws Exception {
        AtomicReference<String> jwt = new AtomicReference<>();
        mockMvc.perform(post("/register/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(myUser)))
                .andExpect(status().isOk());
        mockMvc.perform(post("/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(user)))
                .andExpect(status().is2xxSuccessful())
                .andDo(result -> jwt.set(result.getResponse().getContentAsString()));
        return jwt.get();
    }

    @Test
    @Order(1)
    void shouldGetUsersJwt() throws Exception {
        jwtUser = getJwt(user);
    }

    @Test
    @Order(2)
    void shouldReturnSelectCar() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/cars/1")
                        .header("Authorization", "Bearer " + jwtUser))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id").doesNotExist())
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
        MvcResult mvcResult = mockMvc.perform(get("/cars")
                        .header("Authorization", "Bearer " + jwtUser))
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
    void shouldNoAccessWithoutJwt() throws Exception {
        mockMvc.perform(get("/cars"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    void shouldReturn4xxWhenGetNonexistentCar() throws Exception {
        int actualFreeId = getActualFreeId();
        MvcResult mvcResult = mockMvc.perform(get("/cars/" + actualFreeId)
                        .header("Authorization", "Bearer " + jwtUser))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andReturn();
        String actualMessage = Objects.requireNonNull(mvcResult.getResolvedException()).getMessage();
        Assertions.assertEquals("Nie znaleziono samochodu o id: " + actualFreeId, actualMessage);

    }

    @Test
    void shouldReturnCorrectValueInFirstCar() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/cars/1")
                        .header("Authorization", "Bearer " + jwtUser))
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
                        .header("Authorization", "Bearer " + jwtUser)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(car)))
                .andDo(print())
                .andExpect(status().isOk());

        mockMvc.perform(get("/cars/" + actualFreeId)
                        .header("Authorization", "Bearer " + jwtUser))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").doesNotExist())
                .andExpect(jsonPath("$.make").value("Opel"))
                .andExpect(jsonPath("$.model").value("Astra"))
                .andExpect(jsonPath("$.color").value("red"))
                .andExpect(jsonPath("$.year").value(2015));
    }
}
