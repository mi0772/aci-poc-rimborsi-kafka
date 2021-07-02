package it.almaviva.aci.pocrimborsiconsumer.repository;

import it.almaviva.aci.pocrimborsiconsumer.domain.Crediti;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CreditiRepository extends JpaRepository<Crediti, Integer> {
}
