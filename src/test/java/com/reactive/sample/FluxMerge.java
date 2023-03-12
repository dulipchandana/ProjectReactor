package com.reactive.sample;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;

@Slf4j
public class FluxMerge {

    @Test
    void mergeOperator(){

        Flux<String> flux1 = Flux.just("a" , "b").delayElements(Duration.ofMillis(200));
        Flux<String> flux2 = Flux.just("c" , "d");
        Flux<String> mergeFlux = Flux.merge(flux2,flux1).log();

        mergeFlux.subscribe(log::info);

        StepVerifier.create(mergeFlux)
                .expectSubscription()
                .expectNext("c","d","a","b")
                .expectComplete()
                .verify();
    }
}
