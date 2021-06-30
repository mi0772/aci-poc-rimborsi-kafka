package it.almaviva.aci.pocrimborsiconsumer.routes;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.kafka.KafkaComponent;
import org.apache.camel.component.kafka.KafkaConstants;
import org.apache.camel.component.kafka.KafkaManualCommit;
import org.apache.camel.processor.idempotent.kafka.KafkaIdempotentRepository;
import org.apache.camel.support.SimpleRegistry;
import org.apache.camel.support.processor.idempotent.MemoryIdempotentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RimborsoRoute extends RouteBuilder {

    public RimborsoRoute(CamelContext context) {
        super(context);
    }

    @Value("${idemponent-topid}")
    private String idemponentTopic;

    @Value("${kafka.server}")
    private String kafkaBroker;

    @Value("${kafka.port}")
    private String kafkaPort;

    @Override
    public void configure() throws Exception {

        KafkaIdempotentRepository kafkaIdempotentRepository = new KafkaIdempotentRepository(idemponentTopic, kafkaBroker+":"+kafkaPort);

        from("kafka:{{kafka.topic}}?brokers={{kafka.server}}:{{kafka.port}}&groupId={{kafka.channel}}&autoOffsetReset=earliest&autoCommitEnable=false&allowManualCommit=true")
                .idempotentConsumer(header("kafka.KEY"), kafkaIdempotentRepository)
                .process(new Processor() {
                    public void process(Exchange exchange) throws Exception {
                        System.out.println("Message Body : " + exchange.getIn().getBody());
                        KafkaManualCommit manual = exchange.getIn().getHeader(KafkaConstants.MANUAL_COMMIT, KafkaManualCommit.class);
                        manual.commitSync();
                    }
                });
    }
}
