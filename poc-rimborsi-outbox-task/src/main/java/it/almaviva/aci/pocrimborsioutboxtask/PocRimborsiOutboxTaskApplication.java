package it.almaviva.aci.pocrimborsioutboxtask;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableAsync
@EnableBinding({Sink.class, Source.class})
public class PocRimborsiOutboxTaskApplication {

    public static void main(String[] args) {
        SpringApplication.run(PocRimborsiOutboxTaskApplication.class, args);
    }

}
