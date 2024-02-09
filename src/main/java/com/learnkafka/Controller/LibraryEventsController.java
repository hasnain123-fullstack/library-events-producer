package com.learnkafka.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.learnkafka.Domain.LibraryEvent;
import com.learnkafka.Domain.LibraryEventType;
import com.learnkafka.producer.LibraryEventProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

@RestController
@Slf4j
public class LibraryEventsController {

    private final LibraryEventProducer libraryEventProducer;

    public LibraryEventsController(LibraryEventProducer libraryEventProducer) {
        this.libraryEventProducer = libraryEventProducer;
    }

    @PostMapping("/v1/libraryevent")
    public ResponseEntity<LibraryEvent> postLibraryEvent(
            @RequestBody @Valid LibraryEvent libraryEvent
    ) throws JsonProcessingException, ExecutionException, InterruptedException, TimeoutException {
        log.info("libraryEvent : {} ", libraryEvent);
        //libraryEventProducer.sendLibraryEvent(libraryEvent);
        //libraryEventProducer.sendLibraryEventApproach2(libraryEvent);
        libraryEventProducer.sendLibraryEventApproach3(libraryEvent);
        return ResponseEntity.status(HttpStatus.CREATED).body(libraryEvent);
    }

    @PutMapping("/v1/libraryevent")
    public ResponseEntity<?> updateLibraryEvent(
            @RequestBody @Valid LibraryEvent libraryEvent
    ) throws JsonProcessingException, ExecutionException, InterruptedException, TimeoutException {

        log.info("libraryEvent : {} ", libraryEvent);

        ResponseEntity<String> BAD_REQUEST = validateLibraryEvent(libraryEvent);
        if (BAD_REQUEST != null) return BAD_REQUEST;

        libraryEventProducer.sendLibraryEventApproach3(libraryEvent);

        return ResponseEntity.status(HttpStatus.OK).body(libraryEvent);
    }

    private static ResponseEntity<String> validateLibraryEvent(LibraryEvent libraryEvent) {
        if(libraryEvent.getLibraryEventId() == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please provide libraryEventId");
        }
        if(!libraryEvent.getLibraryEventType().equals(LibraryEventType.UPDATE.toString())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Only update is allowed");
        }
        return null;
    }

}
