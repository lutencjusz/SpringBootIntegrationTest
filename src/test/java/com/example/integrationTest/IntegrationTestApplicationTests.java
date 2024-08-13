package com.example.integrationTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Objects;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * UWAGA: Musi być uruchomiony Docker Desktop i uruchomiona baza danych MsSQL loving_swartz
 */

@SpringBootTest
@AutoConfigureMockMvc
class IntegrationTestApplicationTests {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void shouldReturnSelectCar() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/cars/1"))
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
    void shouldReturn4xxWhenGet() throws Exception {
        final String id = "9";
        MvcResult mvcResult = mockMvc.perform(get("/cars/" + id))
                .andExpect(status().is4xxClientError()).andReturn();
        String actualMessage = Objects.requireNonNull(mvcResult.getResolvedException()).getMessage();
        Assertions.assertEquals("Samochód o id " + id + " nie istnieje", actualMessage);

    }

    @Test
    void shouldReturnCorrectValue() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/cars/1"))
                .andExpect(status().is2xxSuccessful())
                .andReturn();
        Car actualCar = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Car.class);
        Assertions.assertEquals("Audi", actualCar.getMake());
        Assertions.assertEquals("A4", actualCar.getModel());
        Assertions.assertEquals("black", actualCar.getColor());
        Assertions.assertEquals(2019, actualCar.getYear());
    }

}
