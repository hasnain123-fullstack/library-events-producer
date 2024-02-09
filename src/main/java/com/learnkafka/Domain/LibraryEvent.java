package com.learnkafka.Domain;

import com.learnkafka.Controller.LibraryEventsController;
import lombok.Data;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Data
public class LibraryEvent {
    Integer libraryEventId;
    String libraryEventType;
    @NotNull
    @Valid
    Book book;

    public LibraryEvent(Integer libraryEventId, LibraryEventType libraryEventType, Book book) {
        this.libraryEventId = libraryEventId;
        this.libraryEventType = String.valueOf(libraryEventType);
        this.book = book;
    }

    @SpringBootApplication
    @ComponentScan(basePackageClasses = LibraryEventsController.class)
    public static class LibraryEventsProducerApplication {

        public static void main(String[] args) {
            SpringApplication.run(LibraryEventsProducerApplication.class, args);
        }

    }
}
