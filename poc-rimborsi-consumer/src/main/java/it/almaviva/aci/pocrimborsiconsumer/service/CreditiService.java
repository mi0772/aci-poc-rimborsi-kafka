package it.almaviva.aci.pocrimborsiconsumer.service;

import it.almaviva.aci.pocrimborsiconsumer.domain.Crediti;
import it.almaviva.aci.pocrimborsiconsumer.exceptions.CustomException;
import it.almaviva.aci.pocrimborsiconsumer.model.RimborsoDTO;
import it.almaviva.aci.pocrimborsiconsumer.repository.CreditiRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class CreditiService {

    private final CreditiRepository repository;

    @Autowired
    public CreditiService(CreditiRepository repository) {
        this.repository = repository;
    }

    public void salvaCredito(RimborsoDTO rimborso) throws CustomException {
        this.generateCustomException();
        this.generateSystemException();

        var credito = new Crediti();
        credito.setImporto(rimborso.getImporto());
        credito.setNome(rimborso.getNominativoDestinatario());

        this.repository.save(credito);
    }

    private void generateCustomException() throws CustomException {
        var rand = new Random();

        if (rand.nextInt(10) < 5) {
            throw new CustomException("Errore applicativo");
        }
    }

    private void generateSystemException() {
        var rand = new Random();
        if (rand.nextInt(10) < 3) {
            throw new RuntimeException("Errore di sistema");
        }
    }
}
