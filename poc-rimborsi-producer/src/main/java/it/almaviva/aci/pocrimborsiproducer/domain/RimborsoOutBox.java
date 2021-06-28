package it.almaviva.aci.pocrimborsiproducer.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;
import java.math.BigInteger;
import java.util.Date;

@Entity
@Table(name = "rimborsi_outbox")
public class RimborsoOutBox {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "rimborsi_outbox_seq")
    @SequenceGenerator(name="rimborsi_outbox_seq", sequenceName = "rimborsi_outbox_id_seq", allocationSize = 1)
    @Column(name = "id")
    private Integer id;

    @OneToOne
    @JoinColumn(name = "id_rimborso")
    @JsonBackReference
    private Rimborso rimborso;

    @Column(name = "stato")
    private Integer stato;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "timestamp_invio")
    private Date invio;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Rimborso getRimborso() {
        return rimborso;
    }

    public void setRimborso(Rimborso rimborso) {
        this.rimborso = rimborso;
    }

    public Integer getStato() {
        return stato;
    }

    public void setStato(Integer stato) {
        this.stato = stato;
    }

    public Date getInvio() {
        return invio;
    }

    public void setInvio(Date invio) {
        this.invio = invio;
    }
}
