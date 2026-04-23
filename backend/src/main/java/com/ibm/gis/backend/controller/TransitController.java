package com.ibm.gis.backend.controller;

import com.ibm.gis.backend.model.TransitVehicle;
import com.ibm.gis.backend.repository.TransitVehicleRepository;
import com.ibm.gis.backend.dto.TransitVehicleDTO;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/transit")
@CrossOrigin(origins = "*")
public class TransitController {

    private final TransitVehicleRepository repository;

    public TransitController(TransitVehicleRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<TransitVehicleDTO> getAllVehicles() {
        return repository.findAll().stream().map(v -> {
            TransitVehicleDTO dto = new TransitVehicleDTO();
            dto.setId(v.getId());
            dto.setVehicleId(v.getVehicleId());
            dto.setRoute(v.getRoute());
            dto.setAircraftType(v.getAircraftType());
            dto.setOrigin(v.getOrigin());
            dto.setDestination(v.getDestination());
            if (v.getLocation() != null) {
                dto.setLatitude(v.getLocation().getY());
                dto.setLongitude(v.getLocation().getX());
            }
            dto.setLastUpdate(v.getLastUpdate());
            return dto;
        }).collect(Collectors.toList());
    }
}
