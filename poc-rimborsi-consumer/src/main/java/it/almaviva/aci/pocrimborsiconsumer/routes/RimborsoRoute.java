package it.almaviva.aci.pocrimborsiconsumer.routes;

import com.google.gson.Gson;
import it.almaviva.aci.pocrimborsiconsumer.config.KafkaProperties;
import it.almaviva.aci.pocrimborsiconsumer.exceptions.CustomException;
import it.almaviva.aci.pocrimborsiconsumer.model.RimborsoDTO;
import it.almaviva.aci.pocrimborsiconsumer.service.CreditiService;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.kafka.KafkaConstants;
import org.apache.camel.component.kafka.KafkaManualCommit;
import org.apache.camel.processor.idempotent.kafka.KafkaIdempotentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RimborsoRoute extends RouteBuilder {

    public RimborsoRoute(CamelContext context) {
        super(context);
    }

    @Autowired
    private CreditiService service;

    @Autowired
    private KafkaProperties kafkaProps;

    @Autowired
    Processor evaluateExceptionProcessor;

    @Override
    public void configure() throws Exception {

        KafkaIdempotentRepository kafkaIdempotentRepository = new KafkaIdempotentRepository(kafkaProps.getIdempotentTopicId(), kafkaProps.getHostWithPort());

        String kafkaUrl = kafkaProps.buildKafkaUrl();
        log.info("creazione rotta camel per kafka: {}", kafkaUrl);

        onException(Exception.class)
                .handled(false)
                .process(evaluateExceptionProcessor)
                .log(LoggingLevel.WARN, "errore di sistema: ${exception.message}");


        from(kafkaUrl)
                .idempotentConsumer(header("kafka.KEY"), kafkaIdempotentRepository)
                .eager(false)
                .removeOnFailure(true)
                .doTry()
                    .process(exchange -> {
                        log.info("Ricevuto messaggio kafka : {}", exchange.getIn().getBody());

                        var model = new Gson().fromJson(exchange.getIn().getBody().toString(), RimborsoDTO.class);
                        this.service.salvaCredito(model);
                        log.info("salvataggio eseguito con successo: eseguo la commit del messaggio");
                        exchange.getIn().getHeader(KafkaConstants.MANUAL_COMMIT, KafkaManualCommit.class).commitSync();
                    })
                .doCatch(CustomException.class)
                    .process(new Processor() {
                        @Override
                        public void process(Exchange exchange) throws Exception {
                            log.info("errore applicativo gestito, eseguo la commit del messaggio");
                            exchange.getIn().getHeader(KafkaConstants.MANUAL_COMMIT, KafkaManualCommit.class).commitSync();
                        }
                    })
                .log("fine")
        ;
    }
}
