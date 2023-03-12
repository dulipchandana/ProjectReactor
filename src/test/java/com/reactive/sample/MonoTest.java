package com.reactive.sample;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscription;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.function.Consumer;

@Slf4j
public class MonoTest {
    /**
     * Reactive streams
     */
    @Test
    public void monoSubscriber(){
        String testString = "dulip";
        Mono<String> mono = Mono.just(testString)
                .log();
        mono.subscribe();
        StepVerifier.create(mono)
                        .expectNext(testString)
                                .verifyComplete();
        log.info("setup is running-{}",mono);
    }

    @Test
    public void monoSubscriberConsumer(){
        String testString = "dulip";
        Mono<String> mono = Mono.just(testString)
                .log();
        mono.subscribe(s-> log.info("Value {}" , s) );
        log.info("-----------------------------------------");
        StepVerifier.create(mono)
                .expectNext(testString)
                .verifyComplete();
        log.info("setup is running-{}",mono);
    }

    @Test
    public void monoSubscriberConsumerError(){
        String testString = "dulip";
        Mono<String> mono = Mono.just(testString)
                .map(s -> {throw new RuntimeException("throw new mono error");});
        mono.subscribe(s-> log.info("Value {}" , s) , s -> log.error("--error--{}",s.getMessage()) );
        mono.subscribe(s -> log.info(s) , Throwable::printStackTrace);
        log.info("-----------------------------------------");
        StepVerifier.create(mono)
                        .expectError(RuntimeException.class)
                                .verify();
        log.info("setup is running-{}",mono);
    }

    @Test
    public void monoSubscriberConsumerComplete(){
        String testString = "dulip";
        Mono<String> mono = Mono.just(testString)
                .log()
                .map(s -> s.toUpperCase());

        mono.subscribe(s-> log.info("Value {}" , s) );
        mono.subscribe(s -> log.info(s) , Throwable::printStackTrace,
                () -> log.info("Finished"),
                Subscription::cancel);
        log.info("-----------------------------------------");
        StepVerifier.create(mono)
                .expectNext(testString.toUpperCase())
                .verifyComplete();
        log.info("setup is running-{}",mono);
    }

    @Test
    public void monoDoOnMethods(){
        String testString = "dulip";
        Mono<String> mono = Mono.just(testString)
                .log()
                .map(String::toUpperCase)
                .doOnSubscribe(subscription -> log.info("Subcribed ---{}" , subscription))
                .doOnRequest(longNumber -> log.info("OnRequest .."))
                .doOnNext(s -> log.info("do on next {}" , s))
                .doOnSuccess(s -> log.info("do on sucess {}" ,s ));

        mono.subscribe(s-> log.info("Value {}" , s) );
        log.info("-----------------------------------------");
        StepVerifier.create(mono)
                .expectNext(testString.toUpperCase())
                .verifyComplete();
        log.info("setup is running-{}",mono);
    }

    @Test
    void monoDoOnError() {
        Mono<Object> error = Mono.error(new IllegalArgumentException("Mono Error"))
                .doOnError(e -> log.error("Error Message {}", e.getMessage()))
                .doOnNext(s -> log.info("doOnNext-------------"))
                .log();
       // error.subscribe((Consumer<? super Object>) error);
        StepVerifier.create(error)
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void monoDoOnErrorResume() {
        String errorOnResume = "XOXOXO";
        Mono<Object> error = Mono.error(new IllegalArgumentException("Mono Error"))
                .doOnError(e -> log.error("Error Message {}", e.getMessage()))
                .doOnNext(s -> log.info("doOnNext-------------"))
                .onErrorResume(s -> {
                    log.info("Inside On Error Resume");
                    return Mono.just(errorOnResume);
                })
                .log();
        // error.subscribe((Consumer<? super Object>) error);
        StepVerifier.create(error)
                .expectNext(errorOnResume)
                .verifyComplete();
    }


}
