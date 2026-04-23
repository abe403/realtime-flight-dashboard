package com.ibm.gis.backend.controller;

import com.ibm.gis.backend.model.TransitVehicle;
import com.ibm.gis.backend.repository.TransitVehicleRepository;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransitController.class)
public class TransitControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransitVehicleRepository repository;

    @Test
    public void testGetAllVehicles() throws Exception {
        TransitVehicle v = new TransitVehicle();
        v.setId(1L);
        v.setVehicleId("AAL123");
        v.setRoute("USA");
        v.setLocation(new GeometryFactory().createPoint(new Coordinate(-103.34, 20.65)));
        v.setLastUpdate(LocalDateTime.now());

        Mockito.when(repository.findAll()).thenReturn(Collections.singletonList(v));

        mockMvc.perform(get("/api/transit"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].vehicleId").value("AAL123"));
    }
}
