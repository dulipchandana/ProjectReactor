package com.reactive.sample;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;

@Slf4j
public class ConnectableFluxTest {

    @Test
    public void connectableFlux() throws InterruptedException {
        ConnectableFlux<Integer> connectableFlux = Flux.range(1,10)
                .log()
                .delayElements(Duration.ofMillis(100))
                .publish();
        connectableFlux.connect();

        log.info("Thread will be sleeping 300 ms");

        //Thread.sleep(300);

        connectableFlux.subscribe(i -> log.info("sub1 number {}", i));
        log.info ("sleep for 200 ms");
        //Thread.sleep(200);
        connectableFlux.subscribe(i -> log.info("sub2 number {}", i));

        StepVerifier
                .create(connectableFlux)
                .then(ConnectableFlux::concat)
                .thenConsumeWhile(i -> i<=5)
                .expectNext(6,7,8,9,10)
                .expectComplete()
                .verify();

    }

    @Test
    public void connectableFluxAutoConnect() throws InterruptedException {
        Flux<Integer> fluxAutoConnect = Flux.range(1,10)
                .log()
                .delayElements(Duration.ofMillis(100))
                .publish()
                        .autoConnect(2);
        StepVerifier
                .create(fluxAutoConnect)
                .then(fluxAutoConnect::subscribe)
                .thenConsumeWhile(i -> i<=5)
                .expectNext(6,7,8,9,10)
                .expectComplete()
                .verify();

    }

    @Test
    void subscribeOnIo() throws InterruptedException {
        Mono<List<String>> list = Mono.fromCallable(() ->
                Files.readAllLines(Path.of("test-file")))
                .log()
                .subscribeOn(Schedulers.boundedElastic());
        //list.subscribe(s -> log.info("{}" , s));
        //Thread.sleep(2000);

        StepVerifier.create(list)
                .expectSubscription()
                .thenConsumeWhile(l -> {
                    Assertions.assertFalse(l.isEmpty());
                    log.info("size {}",l.size());
                    return true;
                }).verifyComplete();
    }


}
