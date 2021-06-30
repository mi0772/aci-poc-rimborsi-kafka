package it.almaviva.aci.pocrimborsioutboxtask.model;

import java.math.BigDecimal;


public class RimborsoDTO {
    private final String codiceFiscaleDestinatario;
    private final String nominativoDestinatario;
    private final BigDecimal importo;

    private Integer id;

    public RimborsoDTO(String codiceFiscaleDestinatario, String nominativoDestinatario, BigDecimal importo) {
        this.codiceFiscaleDestinatario = codiceFiscaleDestinatario;
        this.nominativoDestinatario = nominativoDestinatario;
        this.importo = importo;
    }

    public String getCodiceFiscaleDestinatario() {
        return codiceFiscaleDestinatario;
    }

    public String getNominativoDestinatario() {
        return nominativoDestinatario;
    }

    public BigDecimal getImporto() {
        return importo;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
