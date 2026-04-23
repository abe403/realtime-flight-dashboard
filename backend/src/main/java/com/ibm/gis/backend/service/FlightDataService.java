package com.ibm.gis.backend.service;

import com.ibm.gis.backend.model.TransitVehicle;
import com.ibm.gis.backend.repository.TransitVehicleRepository;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@EnableScheduling
public class FlightDataService {

    private final TransitVehicleRepository repository;
    private final GeometryFactory geometryFactory = new GeometryFactory();
    private final RestTemplate restTemplate = new RestTemplate();

    public FlightDataService(TransitVehicleRepository repository) {
        this.repository = repository;
    }

    @Scheduled(fixedRate = 10000)
    public void fetchFlightData() {
        try {
            String url = "https://data-cloud.flightradar24.com/zones/fcgi/data.js?bounds=21.5,19.5,-104.5,-102.5";
            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", "Mozilla/5.0");
            HttpEntity<String> entity = new HttpEntity<>("parameters", headers);

            ResponseEntity<Map> responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
            Map<String, Object> response = responseEntity.getBody();

            if (response != null) {
                List<TransitVehicle> existing = repository.findAll();

                for (Map.Entry<String, Object> entry : response.entrySet()) {
                    if (entry.getKey().equals("full_count") || entry.getKey().equals("version") || entry.getKey().equals("stats")) {
                        continue;
                    }

                    if (entry.getValue() instanceof List) {
                        List<Object> state = (List<Object>) entry.getValue();
                        if (state.size() < 17) continue;

                        double latitude = Double.parseDouble(state.get(1).toString());
                        double longitude = Double.parseDouble(state.get(2).toString());
                        String aircraftType = state.get(8) != null ? state.get(8).toString() : "Unknown";
                        String origin = state.get(11) != null ? state.get(11).toString() : "N/A";
                        if(origin.isEmpty()) origin = "N/A";
                        String destination = state.get(12) != null ? state.get(12).toString() : "N/A";
                        if(destination.isEmpty()) destination = "N/A";
                        String flightNumber = state.get(13) != null ? state.get(13).toString() : "Unknown";
                        String callsign = state.get(16) != null ? state.get(16).toString() : flightNumber;
                        if (callsign.isEmpty()) callsign = "UNKNOWN";

                        final String finalCallsign = callsign;

                        Optional<TransitVehicle> vehicleOpt = existing.stream()
                                .filter(v -> v.getVehicleId().equals(finalCallsign))
                                .findFirst();

                        TransitVehicle vehicle = vehicleOpt.orElse(new TransitVehicle());
                        vehicle.setVehicleId(finalCallsign);
                        vehicle.setRoute(flightNumber);
                        vehicle.setAircraftType(aircraftType);
                        vehicle.setOrigin(origin);
                        vehicle.setDestination(destination);
                        vehicle.setLocation(geometryFactory.createPoint(new Coordinate(longitude, latitude)));
                        vehicle.setLastUpdate(LocalDateTime.now());

                        repository.save(vehicle);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error fetching flight data: " + e.getMessage());
        }

        // ALways run cleanup routine even if API fails or throws exceptions
        try {
            LocalDateTime expirationTime = LocalDateTime.now().minusMinutes(5);
            List<TransitVehicle> existing = repository.findAll();
            List<TransitVehicle> staleFlights = existing.stream()
                    .filter(v -> v.getLastUpdate() != null && v.getLastUpdate().isBefore(expirationTime))
                    .collect(java.util.stream.Collectors.toList());
            
            if (!staleFlights.isEmpty()) {
                repository.deleteAll(staleFlights);
            }
        } catch (Exception e) {
            System.err.println("Error cleaning up stale flights: " + e.getMessage());
        }
    }
}
