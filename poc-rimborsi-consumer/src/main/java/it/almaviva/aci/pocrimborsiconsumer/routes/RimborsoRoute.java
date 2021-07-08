package it.almaviva.aci.pocrimborsiconsumer.routes;

import com.google.gson.Gson;
import it.almaviva.aci.pocrimborsiconsumer.config.KafkaProperties;
import it.almaviva.aci.pocrimborsiconsumer.exceptions.CustomException;
import it.almaviva.aci.pocrimborsiconsumer.model.RimborsoDTO;
import it.almaviva.aci.pocrimborsiconsumer.service.CreditiService;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.*;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.kafka.KafkaConstants;
import org.apache.camel.component.kafka.KafkaManualCommit;
import org.apache.camel.processor.idempotent.kafka.KafkaIdempotentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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

    @Autowired
    private KafkaProperties kafkaProps;

    @Autowired
    Processor evaluateExceptionProcessor;

    @Autowired
    private Processor kafkaOffsetManagerProcessor;

    @Override
    public void configure() throws Exception {

        KafkaIdempotentRepository kafkaIdempotentRepository = new KafkaIdempotentRepository(idemponentTopic, kafkaBroker+":"+kafkaPort);

        String kafkaUrl = kafkaProps.buildKafkaUrl();
        log.info("building camel route to consume from kafka: {}", kafkaUrl);

        onException(Exception.class)
                .handled(false)
                .process(evaluateExceptionProcessor)
                .log(LoggingLevel.WARN, "errore di sistema: ${exception.message}");


        //from(kafkaUrl)
        //from("kafka:{{kafka.topic}}?brokers={{kafka.server}}:{{kafka.port}}&groupId={{kafka.channel}}&autoOffsetReset=earliest&autoCommitEnable=false&allowManualCommit=true")
        from(kafkaUrl)
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
                    .process(kafkaOffsetManagerProcessor)
                .doCatch(CustomException.class)
                    .process(new Processor() {
                        @Override
                        public void process(Exchange exchange) throws Exception {
                            log.info("errore applicativo, eseguo la commit del messaggio");
                            exchange.getIn().getHeader(KafkaConstants.MANUAL_COMMIT, KafkaManualCommit.class).commitSync();
                        }
                    })
                .log("fine")
        ;
    }
}
