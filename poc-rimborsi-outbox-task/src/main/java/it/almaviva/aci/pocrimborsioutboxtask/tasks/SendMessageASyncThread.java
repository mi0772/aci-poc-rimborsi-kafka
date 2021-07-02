package it.almaviva.aci.pocrimborsioutboxtask.tasks;

import com.google.gson.Gson;
import it.almaviva.aci.pocrimborsioutboxtask.domain.RimborsoOutBox;
import it.almaviva.aci.pocrimborsioutboxtask.exception.UndeliverableKafkaMessage;
import it.almaviva.aci.pocrimborsioutboxtask.model.RimborsoDTO;
import it.almaviva.aci.pocrimborsioutboxtask.repository.RimborsoOutBoxRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SendMessageASyncThread {

    private final RimborsoOutBoxRepository rimborsoOutBoxRepository;

    private final Source source;

    @Autowired
    public SendMessageASyncThread(RimborsoOutBoxRepository rimborsoOutBoxRepository, Source source) {
        this.rimborsoOutBoxRepository = rimborsoOutBoxRepository;
        this.source = source;
    }

    @Async
    public void sendMessageAndUpdateState(RimborsoOutBox rimborso) {
        try {
            log.info("invio messaggio : {}", rimborso);
            var r = new RimborsoDTO(rimborso.getRimborso().getCodiceFiscaleDestinatario(), rimborso.getRimborso().getNominativoDestinatario(),rimborso.getRimborso().getImporto());

            var messageKey = String.format("K_%d", rimborso.getRimborso().getId());

            source
                    .output()
                    .send(MessageBuilder
                            .withPayload(new Gson().toJson(r))
                            .setHeader(KafkaHeaders.MESSAGE_KEY, messageKey.getBytes())
                            .build());

            this.rimborsoOutBoxRepository.delete(rimborso);
        }
        catch (UndeliverableKafkaMessage e) {
            log.warn("rimborso : {} non inviato", rimborso);
        }
    }
}
