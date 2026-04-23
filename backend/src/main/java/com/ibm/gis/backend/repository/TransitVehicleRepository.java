package com.ibm.gis.backend.repository;

import com.ibm.gis.backend.model.TransitVehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransitVehicleRepository extends JpaRepository<TransitVehicle, Long> {
}
