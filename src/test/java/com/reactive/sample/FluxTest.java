package com.reactive.sample;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.List;

@Slf4j
public class FluxTest {

    @Test
    void fluxTest(){
        Flux<String> fluxString = Flux.just("asd","mnb","rty","mgmd","hihp")
                .log();
        StepVerifier.create(fluxString)
                .expectNext("asd","mnb","rty","mgmd","hihp")
                .verifyComplete();
    }

    @Test
    void fluxTestNumbers(){
        Flux<Integer> fluxString = Flux.range(1,5)
                .log();
        fluxString.subscribe(i -> log.info("the integer {}", i));
        StepVerifier.create(fluxString)
                .expectNext(1,2,3,4,5)
                .verifyComplete();
    }


    @Test
    void fluxSubscribedFromList(){
        Flux<Integer> fluxString = Flux.fromIterable(List.of(1,2,3,4,5))
                .log()
                .map(i -> {
                    if (i ==4)
                        throw new IndexOutOfBoundsException("Index Error");
                    return i;
                });
        fluxString.subscribe(i -> log.info("the integer {}", i) , Throwable::printStackTrace,
                () -> log.info("Done"));
        StepVerifier.create(fluxString)
                .expectNext(1,2,3)
                .expectError(IndexOutOfBoundsException.class)
                .verify();
    }

    @Test
    void fluxSubscribedNumbersUglyBackpressure(){
        Flux<Integer> fluxString = Flux.fromIterable(List.of(1,2,3,4,5,6,7,8,9,10))
                .log();
        fluxString.subscribe(new Subscriber<Integer>() {
            private int count =0;
            private Subscription subscription;

            private int requestCount = 2;

            @Override
            public void onSubscribe(Subscription s) {
                this.subscription = s;
                subscription.request(requestCount);
            }

            @Override
            public void onNext(Integer integer) {
                count ++;
                if (count >= requestCount) {
                    count = 0 ;
                    subscription.request(requestCount);
                }
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onComplete() {

            }
        });
        fluxString.subscribe(i -> log.info("the integer {}", i) , Throwable::printStackTrace,
                () -> log.info("Done"));
        StepVerifier.create(fluxString)
                .expectNext(1,2,3,4,5,6,7,8,9,10)
                .verifyComplete();
    }

    @Test
    void fluxSubscriberInterval() throws InterruptedException {
        Flux<Long> interval = Flux.interval(Duration.ofMillis(100))
                .take(10)
                .log();
        interval.subscribe(i -> log.info("Numbers {}" , i));
        Thread.sleep(3000);

    }

    @Test
    void fluxSubscriberIntervalVerify() throws InterruptedException {
        Flux<Long> interval = Flux.interval(Duration.ofMillis(100))
                .take(10)
                .log();
        StepVerifier.withVirtualTime(() -> interval)
                .verifyComplete();

    }
}

