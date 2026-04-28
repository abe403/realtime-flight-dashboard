package com.ibm.gis.backend.service;

import com.ibm.gis.backend.model.TransitVehicle;
import com.ibm.gis.backend.repository.TransitVehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class FlightDataServiceTest {

    @Mock
    private TransitVehicleRepository repository;

    @Mock
    private RestTemplate restTemplate;

    private FlightDataService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new FlightDataService(repository, restTemplate);
    }

    @Test
    void testFetchFlightData_Success() {
        // Mock data from FlightRadar24 format
        Map<String, Object> mockResponse = new HashMap<>();
        List<Object> flightInfo = Arrays.asList(
            "HEX", 20.65, -103.34, 180, 15000, 450, "1234", "F-TYPE", "A320", 
            12345678, "MEX", "MEX-AIRPORT", "GDL", "AM123", 0, "AAL123", "CALLSIGN"
        );
        mockResponse.put("AAL123", flightInfo);
        mockResponse.put("full_count", 1);

        ResponseEntity<Map> responseEntity = ResponseEntity.ok(mockResponse);
        
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(Map.class)))
            .thenReturn(responseEntity);
        when(repository.findAll()).thenReturn(new ArrayList<>());

        service.fetchFlightData();

        ArgumentCaptor<TransitVehicle> captor = ArgumentCaptor.forClass(TransitVehicle.class);
        verify(repository, atLeastOnce()).save(captor.capture());

        TransitVehicle saved = captor.getValue();
        assertEquals("CALLSIGN", saved.getVehicleId());
        assertEquals("AM123", saved.getRoute());
        assertEquals(20.65, saved.getLocation().getY());
        assertEquals(-103.34, saved.getLocation().getX());
    }
}
