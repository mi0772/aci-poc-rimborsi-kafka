package it.almaviva.aci.pocrimborsiconsumer.routes;

import com.google.gson.Gson;
import it.almaviva.aci.pocrimborsiconsumer.exceptions.CustomException;
import it.almaviva.aci.pocrimborsiconsumer.model.RimborsoDTO;
import it.almaviva.aci.pocrimborsiconsumer.service.CreditiService;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.RuntimeCamelException;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.kafka.KafkaConstants;
import org.apache.camel.component.kafka.KafkaManualCommit;
import org.apache.camel.processor.idempotent.kafka.KafkaIdempotentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RimborsoRoute extends RouteBuilder {

    public RimborsoRoute(CamelContext context) {
        super(context);
    }

    @Value("${kafka.idemponent-topid}")
    private String idemponentTopic;

    @Value("${kafka.server}")
    private String kafkaBroker;

    @Value("${kafka.port}")
    private String kafkaPort;

    @Autowired
    private CreditiService service;

    @Override
    public void configure() throws Exception {

        KafkaIdempotentRepository kafkaIdempotentRepository = new KafkaIdempotentRepository(idemponentTopic, kafkaBroker+":"+kafkaPort);



        from("kafka:{{kafka.topic}}?brokers={{kafka.server}}:{{kafka.port}}&groupId={{kafka.channel}}&autoOffsetReset=earliest&autoCommitEnable=false&allowManualCommit=true")
                .idempotentConsumer(header("kafka.KEY"), kafkaIdempotentRepository)
                .eager(false)
                .removeOnFailure(true)
                .doTry()
                    .process(exchange -> {
                        log.info("Ricevuto messaggio kafka : {}", exchange.getIn().getBody());

                        var model = new Gson().fromJson(exchange.getIn().getBody().toString(), RimborsoDTO.class);
                        this.service.salvaCredito(model);

                        exchange.getIn().getHeader(KafkaConstants.MANUAL_COMMIT, KafkaManualCommit.class).commitSync();
                    })
                .doCatch(CustomException.class)
                    .process(exchange -> {
                        log.info("Exception applicativa, eseguo la commit del messaggio");
                        exchange.getIn().getHeader(KafkaConstants.MANUAL_COMMIT, KafkaManualCommit.class).commitSync();
                    })
                .doCatch(RuntimeCamelException.class)
                    .process(exchange -> {
                        log.info("Exception di camel ... non eseguo la commit");
                    })
                .doCatch(Exception.class)
                    .process(exchange -> {
                        log.info("Exception di sistema, non eseguo la commit del messaggio");
                    })
        ;
    }
}
