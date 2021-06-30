package it.almaviva.aci.pocrimborsioutboxtask.tasks;

import com.google.gson.Gson;
import it.almaviva.aci.pocrimborsioutboxtask.domain.RimborsoOutBox;
import it.almaviva.aci.pocrimborsioutboxtask.exception.UndeliverableKafkaMessage;
import it.almaviva.aci.pocrimborsioutboxtask.model.RimborsoDTO;
import it.almaviva.aci.pocrimborsioutboxtask.repository.RimborsoOutBoxRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.UUID;

@Component
@Slf4j
public class SendMessageASyncThread {

    @Value("${app.kafka.topic-name}")
    private String topicName;

    private final RimborsoOutBoxRepository rimborsoOutBoxRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    public SendMessageASyncThread(RimborsoOutBoxRepository rimborsoOutBoxRepository, KafkaTemplate<String, String> kafkaTemplate) {
        this.rimborsoOutBoxRepository = rimborsoOutBoxRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Async
    public void sendMessageAndUpdateState(RimborsoOutBox rimborso) {
        try {
            log.info("invio messaggio : {}", rimborso);
            var r = new RimborsoDTO(rimborso.getRimborso().getCodiceFiscaleDestinatario(), rimborso.getRimborso().getNominativoDestinatario(),rimborso.getRimborso().getImporto());

            var messageKey = String.format("K_%d", rimborso.getRimborso().getId());

            this.sendMessage(new Gson().toJson(r), topicName, messageKey);

            //TODO: Ne mando 2 per provare l'idemponence
            this.sendMessage(new Gson().toJson(r), topicName, messageKey);

            this.rimborsoOutBoxRepository.delete(rimborso);
        }
        catch (UndeliverableKafkaMessage e) {
            log.warn("rimborso : {} non inviato", rimborso);
        }
    }

    private void sendMessage(String rimborso, String topicName, String messageKey) {

        ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send(topicName, messageKey, rimborso);

        future.addCallback(new ListenableFutureCallback<>() {

            @Override
            public void onSuccess(SendResult<String, String> result) {
                log.info("Sent message=[" + rimborso + "] with offset=[" + result.getRecordMetadata().offset() + "]");
            }

            @Override
            public void onFailure(Throwable ex) {
                log.error("Unable to send message=["+ rimborso + "] due to : " + ex.getMessage());
                throw new UndeliverableKafkaMessage();
            }
        });
    }
}
