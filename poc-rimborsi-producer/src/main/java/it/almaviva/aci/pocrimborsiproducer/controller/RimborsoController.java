package it.almaviva.aci.pocrimborsiproducer.controller;

import it.almaviva.aci.pocrimborsiproducer.model.RimborsoDTO;
import it.almaviva.aci.pocrimborsiproducer.service.RimborsoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rimborso")
public class RimborsoController {

    private final RimborsoService rimborsoService;

    @Autowired
    public RimborsoController(RimborsoService rimborsoService) {
        this.rimborsoService = rimborsoService;
    }

    @PostMapping()
    public ResponseEntity<RimborsoDTO> create(@RequestBody RimborsoDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.rimborsoService.process(request));
    }
}
