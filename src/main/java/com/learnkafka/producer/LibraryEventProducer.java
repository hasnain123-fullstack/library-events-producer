package com.learnkafka.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learnkafka.Domain.LibraryEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Component
@Slf4j
public class LibraryEventProducer {

    public String topic = "library-events";

    public final KafkaTemplate<Integer, String> kafkaTemplate;

    public final ObjectMapper objectMapper;

    public LibraryEventProducer(KafkaTemplate<Integer, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public CompletableFuture<SendResult<Integer, String>> sendLibraryEvent(LibraryEvent libraryEvent) throws JsonProcessingException {
        var key = libraryEvent.getLibraryEventId();
        var value = objectMapper.writeValueAsString(libraryEvent);

        ListenableFuture<SendResult<Integer, String >> listenableFuture = kafkaTemplate.send(topic, key, value);

        return listenableFuture.completable().whenComplete((sendResult, throwable) -> {
            if (throwable != null) {
                handleFailure(key, value, throwable);
            } else {
                handleSuccess(key, value, sendResult);
            }
        });
    }

    public SendResult<Integer, String> sendLibraryEventApproach2(LibraryEvent libraryEvent) throws JsonProcessingException, ExecutionException, InterruptedException, TimeoutException {
        var key = libraryEvent.getLibraryEventId();
        var value = objectMapper.writeValueAsString(libraryEvent);
// synchronous call
        // 1. calls got block then checks the meatdata of kafka cluster
        // 2. block and wait until the message is sent
        SendResult<Integer, String> listenableFuture = kafkaTemplate.send(topic, key, value).get(3, TimeUnit.SECONDS);
        handleSuccess(key, value, listenableFuture);
        return listenableFuture;
    }

    public CompletableFuture<SendResult<Integer, String>> sendLibraryEventApproach3(LibraryEvent libraryEvent) throws JsonProcessingException {
        var key = libraryEvent.getLibraryEventId();
        var value = objectMapper.writeValueAsString(libraryEvent);

        var producerRecord = buildProducerRecord(key, value);

        ListenableFuture<SendResult<Integer, String >> listenableFuture = kafkaTemplate.send(producerRecord);

        return listenableFuture.completable().whenComplete((sendResult, throwable) -> {
            if (throwable != null) {
                handleFailure(key, value, throwable);
            } else {
                handleSuccess(key, value, sendResult);
            }
        });
    }

    private ProducerRecord<Integer, String> buildProducerRecord(Integer key, String value) {
        List<Header> headers = List.of(new RecordHeader("event-source", "scanner".getBytes()));
        return new ProducerRecord<>(topic, null, key, value, headers);
    }

    private void handleSuccess(Integer key, String value, SendResult<Integer, String> sendResult) {
        log.info("The message is successfully sent for key {} and value {} , partition is {}",
                key, value, sendResult.getRecordMetadata());
    }

    private void handleFailure(Integer key, String value, Throwable exp) {
        log.error("Error sending the message and error is {} ", exp.getMessage(), exp);
    }
}
