package it.almaviva.aci.pocrimborsiproducer.repository;

import it.almaviva.aci.pocrimborsiproducer.domain.Rimborso;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RimborsoRepository extends JpaRepository<Rimborso, Integer> {
}
