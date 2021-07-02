package it.almaviva.aci.pocrimborsiconsumer.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "crediti")
@Getter
@Setter
public class Crediti {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "crediti_seq")
    @SequenceGenerator(name="crediti_seq", sequenceName = "crediti_id_seq", allocationSize = 1)
    @Column(name = "id")
    private Integer id;

    @Column(name = "nome")
    private String nome;

    @Column(name = "importo")
    private BigDecimal importo;
}
