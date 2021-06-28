package it.almaviva.aci.pocrimborsiproducer.tasks;

import it.almaviva.aci.pocrimborsiproducer.repository.RimborsoOutBoxRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RimborsoOutboxTask {

    private final RimborsoOutBoxRepository rimborsoOutBoxRepository;
    private final SendMessageASyncThread sendMessageAndUpdateState;

    @Autowired
    public RimborsoOutboxTask(RimborsoOutBoxRepository rimborsoOutBoxRepository, SendMessageASyncThread sendMessageAndUpdateState) {
        this.rimborsoOutBoxRepository = rimborsoOutBoxRepository;
        this.sendMessageAndUpdateState = sendMessageAndUpdateState;
    }

    @Scheduled(fixedDelayString = "${app.task.delay:5000}")
    public void processOutBox() {
        log.info("query outbox per invio evento a kafka");
        this.rimborsoOutBoxRepository.findByStato(0).forEach(sendMessageAndUpdateState::sendMessageAndUpdateState);
        log.info("fine");
    }
}
