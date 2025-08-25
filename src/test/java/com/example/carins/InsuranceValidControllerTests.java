package com.example.carins;

import com.example.carins.model.Car;
import com.example.carins.model.InsurancePolicy;
import com.example.carins.model.Owner;
import com.example.carins.repo.CarRepository;
import com.example.carins.repo.InsurancePolicyRepository;
import com.example.carins.repo.OwnerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.containsString;



@SpringBootTest
@AutoConfigureMockMvc
public class InsuranceValidControllerTests {


        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private CarRepository carRepository;

        @Autowired
        private InsurancePolicyRepository policyRepository;
        @Autowired
        private OwnerRepository ownerRepository;

        @Test
        void shouldReturn404IfCarNotFound() throws Exception {
            mockMvc.perform(get("/api/cars/9999/insurance-valid")
                            .param("date", "2025-08-25"))
                    .andExpect(status().isNotFound());
        }

    @Test
    void shouldReturn400ForInvalidDateFormat() throws Exception {
        Owner owner = new Owner();
        owner.setName("TestOwner");
        ownerRepository.save(owner);

        Car car = new Car(
                "VIN12345",
                "TestMake",
                "TestModel",
                2020,
                owner
        );
        carRepository.save(car);
        mockMvc.perform(get("/api/cars/" + car.getId() + "/insurance-valid")
                        .param("date", "25-08-2025")) // format greșit
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Invalid date format")));
    }

        @Test
        void shouldReturn400ForDateOutOfRange() throws Exception {
            Owner owner = new Owner();
            owner.setName("TestOwner");
            ownerRepository.save(owner);

            Car car = new Car(
                    "VIN12345",
                    "TestMake",
                    "TestModel",
                    2020,
                    owner
            );
            carRepository.save(car);

            mockMvc.perform(get("/api/cars/" + car.getId() + "/insurance-valid")
                            .param("date", "1800-01-01"))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(containsString("out of supported range")));
        }

        @Test
        void shouldReturnValidWhenPolicyExists() throws Exception {
            Owner owner = new Owner();
            owner.setName("TestOwner");
            ownerRepository.save(owner);

            Car car = new Car(
                    "VIN12345",
                    "TestMake",
                    "TestModel",
                    2020,
                    owner
            );
            carRepository.save(car);
            policyRepository.save(new InsurancePolicy(
                    car,
                    "TestProvider",
                    LocalDate.of(2025, 1, 1),
                    LocalDate.of(2025, 12, 31)
            ));

            mockMvc.perform(get("/api/cars/" + car.getId() + "/insurance-valid")
                            .param("date", "2025-08-25"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("VALID"));
        }

        @Test
        void shouldReturnNotValidWhenNoPolicyCoversDate() throws Exception {
            Owner owner = new Owner();
            owner.setName("TestOwner");
            ownerRepository.save(owner);

            Car car = new Car(
                    "VIN12345",
                    "TestMake",
                    "TestModel",
                    2020,
                    owner
            );
            carRepository.save(car);
            policyRepository.save(new InsurancePolicy(
                    car,
                    "TestProvider",
                    LocalDate.of(2025, 1, 1),
                    LocalDate.of(2025, 6, 30)
            ));

            mockMvc.perform(get("/api/cars/" + car.getId() + "/insurance-valid")
                            .param("date", "2025-08-25"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("NOT VALID"));
        }
}
