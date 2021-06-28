package it.almaviva.aci.pocrimborsiproducer.service;

import it.almaviva.aci.pocrimborsiproducer.domain.Rimborso;
import it.almaviva.aci.pocrimborsiproducer.domain.RimborsoOutBox;
import it.almaviva.aci.pocrimborsiproducer.model.RimborsoDTO;
import it.almaviva.aci.pocrimborsiproducer.repository.RimborsoOutBoxRepository;
import it.almaviva.aci.pocrimborsiproducer.repository.RimborsoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RimborsoService {

    private final RimborsoRepository rimborsoRepository;
    private final RimborsoOutBoxRepository rimborsoOutBoxRepository;

    @Autowired
    public RimborsoService(RimborsoRepository rimborsoRepository, RimborsoOutBoxRepository rimborsoOutBoxRepository) {
        this.rimborsoRepository = rimborsoRepository;
        this.rimborsoOutBoxRepository = rimborsoOutBoxRepository;
    }

    @Transactional
    public RimborsoDTO process(RimborsoDTO request) {
        var rimborso = new Rimborso();
        rimborso.setCodiceFiscaleDestinatario(request.getCodiceFiscaleDestinatario());
        rimborso.setImporto(request.getImporto());
        rimborso.setNominativoDestinatario(request.getNominativoDestinatario());

        var rimborsoOutbox = new RimborsoOutBox();
        rimborsoOutbox.setRimborso(rimborso);
        rimborsoOutbox.setStato(0);

        rimborso.setRimborsoOutBox(rimborsoOutbox);

        this.rimborsoRepository.save(rimborso);
        this.rimborsoOutBoxRepository.save(rimborsoOutbox);

        request.setId(rimborso.getId());
        return request;
    }
}
