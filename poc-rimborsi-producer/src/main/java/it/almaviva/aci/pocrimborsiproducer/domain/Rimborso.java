package it.almaviva.aci.pocrimborsiproducer.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.BigInteger;

@Entity
@Table(name = "rimborsi")
public class Rimborso {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "rimborsi_seq")
    @SequenceGenerator(name="rimborsi_seq", sequenceName = "rimborsi_id_seq", allocationSize = 1)
    @Column(name = "id")
    private Integer id;

    @OneToOne
    @JoinColumn(name = "id")
    @JsonBackReference
    private RimborsoOutBox rimborsoOutBox;

    @Column(name = "cf_destinatario")
    private String codiceFiscaleDestinatario;

    @Column(name = "nominativo_destinatario")
    private String nominativoDestinatario;

    @Column(name = "importo")
    private BigDecimal importo;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public RimborsoOutBox getRimborsoOutBox() {
        return rimborsoOutBox;
    }

    public void setRimborsoOutBox(RimborsoOutBox rimborsoOutBox) {
        this.rimborsoOutBox = rimborsoOutBox;
    }

    public String getCodiceFiscaleDestinatario() {
        return codiceFiscaleDestinatario;
    }

    public void setCodiceFiscaleDestinatario(String codiceFiscaleDestinatario) {
        this.codiceFiscaleDestinatario = codiceFiscaleDestinatario;
    }

    public String getNominativoDestinatario() {
        return nominativoDestinatario;
    }

    public void setNominativoDestinatario(String nominativoDestinatario) {
        this.nominativoDestinatario = nominativoDestinatario;
    }

    public BigDecimal getImporto() {
        return importo;
    }

    public void setImporto(BigDecimal importo) {
        this.importo = importo;
    }
}
