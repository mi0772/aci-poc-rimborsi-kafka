package it.almaviva.aci.pocrimborsiproducer.repository;

import it.almaviva.aci.pocrimborsiproducer.domain.RimborsoOutBox;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RimborsoOutBoxRepository extends JpaRepository<RimborsoOutBox, Integer> {
}
